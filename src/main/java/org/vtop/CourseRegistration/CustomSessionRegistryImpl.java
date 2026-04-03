package org.vtop.CourseRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.AbstractSessionEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionIdChangedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.Assert;

public class CustomSessionRegistryImpl implements SessionRegistry, ApplicationListener<AbstractSessionEvent> {


        private static final Logger LOG = LoggerFactory.getLogger(CustomSessionRegistryImpl.class);

        private final String hashReference1 = "PRINCIPALS";

        private final String hashReference2 = "SESSIONSIDS";

        private HashOperations<String, String, ConcurrentMap<Object, Set<String>>> principalOperations;
        private HashOperations<String, String, Map<String, SessionInformation>> sessionOperations;

        public CustomSessionRegistryImpl() {
        }

        public CustomSessionRegistryImpl(RedisTemplate<String, ConcurrentMap<Object, Set<String>>> principalTemplate,
        RedisTemplate<String, Map<String, SessionInformation>> sessionIdTemplate) {
                LOG.info("entered initialization-1");
                this.principalOperations=principalTemplate.opsForHash();
                this.sessionOperations=sessionIdTemplate.opsForHash();
        }


        public CustomSessionRegistryImpl(ConcurrentMap<Object, Set<String>> principals,
                        Map<String, SessionInformation> sessionIds) {
                LOG.info("entered initialization-2");
                if (principals != null) {
                        principals.forEach((k, p) -> {
                                ConcurrentMap<Object, Set<String>> tmpPrincipals = new ConcurrentHashMap<Object, Set<String>>();
                                tmpPrincipals.put(k, p);
                                this.principalOperations.put(hashReference1, String.valueOf(k), tmpPrincipals);
                        });
                }
                if (sessionIds != null) {
                        sessionIds.forEach((k, v) -> {
                                Map<String, SessionInformation> tmpSessionIds = new ConcurrentHashMap<>();
                                tmpSessionIds.put(k, v);
                                this.sessionOperations.put(hashReference2, k, tmpSessionIds);
                        });
                }
        }

        @Override
        public List<Object> getAllPrincipals() {
                LOG.info("entered get all principals");
                ConcurrentMap<Object, Set<String>> tmpPrincipals = new ConcurrentHashMap<Object, Set<String>>();
                Map<String, ConcurrentMap<Object, Set<String>>> tmpPrincipalHolder = this.principalOperations
                                .entries(hashReference1);
                if (tmpPrincipalHolder != null) {
                        LOG.info("success-gap");
                        tmpPrincipalHolder.forEach((y, z) -> {
                                z.forEach((x, v) -> {
                                        tmpPrincipals.put(x, v);
                                });
                        });
                }
                LOG.info("failure-gap");
                return new ArrayList<>(tmpPrincipals.keySet());
        }

        @Override
        public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
                LOG.info("entered get all sessions principal={}",principal);

                ConcurrentMap<Object, Set<String>> tmpPrincipal = this.principalOperations.get(hashReference1,
                                String.valueOf(principal));
                if (tmpPrincipal != null) {
                        
                        Set<String> sessionsUsedByPrincipal = tmpPrincipal.get(principal);
                        if (sessionsUsedByPrincipal == null) {
                                LOG.info("failure-gas-1");
                                return Collections.emptyList();
                        }
                        List<SessionInformation> list = new ArrayList<>(sessionsUsedByPrincipal.size());
                        for (String sessionId : sessionsUsedByPrincipal) {
                                SessionInformation sessionInformation = getSessionInformation(sessionId);
                                if (sessionInformation == null) {
                                        continue;
                                }
                                if (includeExpiredSessions || !sessionInformation.isExpired()) {
                                        list.add(sessionInformation);
                                }
                        }
                        LOG.info("success-gas size={}",list.size());
                        return list;
                } else {
                        LOG.info("failure-gas-2");
                        return Collections.emptyList();
                }
        }

        @Override
        public SessionInformation getSessionInformation(String sessionId) {
                Assert.hasText(sessionId, "SessionId required as per interface contract");
                Map<String, SessionInformation> tmpSessionIds = this.sessionOperations.get(hashReference2, sessionId);
                if (tmpSessionIds != null) {
                        LOG.info("get session information sessionid={},isExists={}",sessionId,(tmpSessionIds.get(sessionId)!=null));
                        return tmpSessionIds.get(sessionId);
                }
                LOG.info("failure gsi");
                return null;
        }

        @Override
        public void onApplicationEvent(AbstractSessionEvent event) {
                LOG.info("entered application event");

                if (event instanceof SessionDestroyedEvent) {

                        LOG.info("entered application destroy event");
                        SessionDestroyedEvent sessionDestroyedEvent = (SessionDestroyedEvent) event;
                        String sessionId = sessionDestroyedEvent.getId();
                        removeSessionInformation(sessionId);

                } else if (event instanceof SessionIdChangedEvent) {

                        LOG.info("entered application change event");
                        SessionIdChangedEvent sessionIdChangedEvent = (SessionIdChangedEvent) event;
                        String oldSessionId = sessionIdChangedEvent.getOldSessionId();
                        Map<String, SessionInformation> tmpSessionIds = this.sessionOperations.get(hashReference2,
                                        oldSessionId);
                        if (tmpSessionIds != null) {
                                Object principal = tmpSessionIds.get(oldSessionId).getPrincipal();
                                removeSessionInformation(oldSessionId);
                                registerNewSession(sessionIdChangedEvent.getNewSessionId(), principal);
                        }
                }
        }


        @Override
        public void refreshLastRequest(String sessionId) {

                LOG.info("entered refresh request event sessionid={}",sessionId);

                Assert.hasText(sessionId, "SessionId required as per interface contract");
                SessionInformation info = getSessionInformation(sessionId);
                if (info != null) {
                        LOG.info("success-rre");
                        info.refreshLastRequest();
                }
        }

        @Override
        public void registerNewSession(String sessionId, Object principal) {

                LOG.info("entered new registration sessionid={},principal={}",sessionId,principal);

                Assert.hasText(sessionId, "SessionId required as per interface contract");
                Assert.notNull(principal, "Principal required as per interface contract");
                if (getSessionInformation(sessionId) != null) {
                        removeSessionInformation(sessionId);
                }

                Map<String, SessionInformation> tmpSessionIds = new HashMap<String, SessionInformation>();
                tmpSessionIds.put(sessionId, new SessionInformation(principal, sessionId, new Date()));
                this.sessionOperations.put(hashReference2, sessionId, tmpSessionIds);
                
                ConcurrentMap<Object, Set<String>> tmpPrincipals = this.principalOperations.get(hashReference1,
                                String.valueOf(principal));
                if (tmpPrincipals != null) {
                        LOG.info("success-rns-1");
                        tmpPrincipals.compute(principal, (key, sessionsUsedByPrincipal) -> {
                                if (sessionsUsedByPrincipal == null) {
                                        sessionsUsedByPrincipal = new CopyOnWriteArraySet<>();
                                }
                                sessionsUsedByPrincipal.add(sessionId);
                                return sessionsUsedByPrincipal;
                        });
                } else {
                        LOG.info("failure-rns-1");
                        tmpPrincipals = new ConcurrentHashMap<Object, Set<String>>();
                        Set<String> sessionsUsedByPrincipal = new CopyOnWriteArraySet<>();
                        sessionsUsedByPrincipal.add(sessionId);
                        tmpPrincipals.put(String.valueOf(principal), sessionsUsedByPrincipal);
                }

                this.principalOperations.put(hashReference1, String.valueOf(principal), tmpPrincipals);
        }

        @Override
        public void removeSessionInformation(String sessionId) {

                LOG.info("entered remove registration sessionid={}",sessionId);

                Assert.hasText(sessionId, "SessionId required as per interface contract");
                SessionInformation info = getSessionInformation(sessionId);
                if (info == null) {
                        LOG.info("failure-rsi");
                        return;
                }

                Map<String, SessionInformation> tmpSessionIds = this.sessionOperations.get(hashReference2, sessionId);
                if (tmpSessionIds != null) {
                        LOG.info("session id={} removed from session", sessionId);
                        this.sessionOperations.delete(hashReference2, sessionId);
                }

                ConcurrentMap<Object, Set<String>> tmpPrincipals = this.principalOperations.get(hashReference1,
                                String.valueOf(info.getPrincipal()));
                if (tmpPrincipals != null) {
                        LOG.info("success-rsi-1");
                        tmpPrincipals.computeIfPresent(info.getPrincipal(), (key, sessionsUsedByPrincipal) -> {
                                LOG.info("session id={} removed from principal={}", sessionId, info.getPrincipal());
                                sessionsUsedByPrincipal.remove(sessionId);
                                if (sessionsUsedByPrincipal.isEmpty()) {
                                        sessionsUsedByPrincipal = null;
                                }

                                return sessionsUsedByPrincipal;
                        });
                        this.principalOperations.put(hashReference1, String.valueOf(info.getPrincipal()),
                                        tmpPrincipals);
                }
        }

        public ConcurrentMap<Object, Set<String>> getAllPrincipalDetails() {
              
                ConcurrentMap<Object, Set<String>> tmpPrincipals = new ConcurrentHashMap<Object, Set<String>>();
                Map<String, ConcurrentMap<Object, Set<String>>> tmpPrincipalHolder = this.principalOperations
                                .entries(hashReference1);

                if (tmpPrincipalHolder != null) {
                        tmpPrincipalHolder.forEach((y, z) -> {
                                z.forEach((x, v) -> {
                                        tmpPrincipals.put(x, v);
                                });
                        });
                }

                return tmpPrincipals;
        }

        public Map<String, SessionInformation> getAllSessionDetails() {

                Map<String, SessionInformation> tmpSessions = new ConcurrentHashMap<>();
                Map<String, Map<String, SessionInformation>> tmpSessionsHolder = this.sessionOperations
                                .entries(hashReference2);
                if (tmpSessionsHolder != null) {
                        tmpSessionsHolder.forEach((y, z) -> {
                                z.forEach((x, v) -> {
                                        tmpSessions.put(x, v);
                                });
                        });
                }
                return tmpSessions;
        }

}

// ConcurrentMap<Object, Set<String>> tmpPrincipals = new
// ConcurrentHashMap<Object, Set<String>>();

// Map<String, SessionInformation> tmpSessionIds = new ConcurrentHashMap<String,
// SessionInformation>();

// Map<String, ConcurrentMap<Object, Set<String>>> tmpPrincipalHolder =
// principalOperations
// .entries(hashReference1);if(tmpPrincipalHolder!=null)
// {
// tmpPrincipalHolder.forEach((y, z) -> {
// z.forEach((x, v) -> {
// tmpPrincipals.put(x, v);
// });
// });
// }

// Map<String, Map<String, SessionInformation>> tmpSessionIdHolder =
// sessionOperations
// .entries(hashReference2);

// if(tmpSessionIds!=null)
// {
// tmpSessionIdHolder.forEach((y, z) -> {
// z.forEach((x, v) -> {
// tmpSessionIds.put(x, v);
// });
// });
// }this.principals=tmpPrincipals;this.sessionIds=tmpSessionIds;

// /
