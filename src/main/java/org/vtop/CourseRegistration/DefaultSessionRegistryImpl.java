package org.vtop.CourseRegistration;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.AbstractSessionEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionIdChangedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.Assert;

public class DefaultSessionRegistryImpl implements SessionRegistry, ApplicationListener<AbstractSessionEvent> {
        
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionRegistryImpl.class);

	// <principal:Object,SessionIdSet>
	private final ConcurrentMap<Object, Set<String>> principals;

	// <sessionId:Object,SessionInformation>
	private final Map<String, SessionInformation> sessionIds;

	public DefaultSessionRegistryImpl() {
		LOG.info("entered initialization-1");
		this.principals = new ConcurrentHashMap<>();
		this.sessionIds = new ConcurrentHashMap<>();
	}

	public DefaultSessionRegistryImpl(ConcurrentMap<Object, Set<String>> principals,
			Map<String, SessionInformation> sessionIds) {
		LOG.info("entered initialization-2");
		this.principals = principals;
		this.sessionIds = sessionIds;
	}

	@Override
	public List<Object> getAllPrincipals() {
		LOG.info("entered get all principals ={}",this.principals.size());
		return new ArrayList<>(this.principals.keySet());
	}

	@Override
	public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
		LOG.info("entered get all sessions principal={}",principal);

		Set<String> sessionsUsedByPrincipal = this.principals.get(principal);
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
	}

	@Override
	public SessionInformation getSessionInformation(String sessionId) {
		LOG.info("get session information sessionid={},isExists={}",sessionId,(this.sessionIds.get(sessionId)!=null));
		Assert.hasText(sessionId, "SessionId required as per interface contract");
		return this.sessionIds.get(sessionId);
	}

	@Override
	public void onApplicationEvent(AbstractSessionEvent event) {
		LOG.info("entered application event");
		if (event instanceof SessionDestroyedEvent) {
			LOG.info("entered application destroy event");
			SessionDestroyedEvent sessionDestroyedEvent = (SessionDestroyedEvent) event;
			String sessionId = sessionDestroyedEvent.getId();
			removeSessionInformation(sessionId);
		}
		else if (event instanceof SessionIdChangedEvent) {
			LOG.info("entered application change event");
			SessionIdChangedEvent sessionIdChangedEvent = (SessionIdChangedEvent) event;
			String oldSessionId = sessionIdChangedEvent.getOldSessionId();
			if (this.sessionIds.containsKey(oldSessionId)) {
				Object principal = this.sessionIds.get(oldSessionId).getPrincipal();
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
		
		this.sessionIds.put(sessionId, new SessionInformation(principal, sessionId, new Date()));

		this.principals.compute(principal, (key, sessionsUsedByPrincipal) -> {
			if (sessionsUsedByPrincipal == null) {
				sessionsUsedByPrincipal = new CopyOnWriteArraySet<>();
			}
			sessionsUsedByPrincipal.add(sessionId);
			return sessionsUsedByPrincipal;
		});

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
		LOG.info("session id={} removed from session", sessionId);
		this.sessionIds.remove(sessionId);
		this.principals.computeIfPresent(info.getPrincipal(), (key, sessionsUsedByPrincipal) -> {
			LOG.info("session id={} removed from principal={}",sessionId,info.getPrincipal());
			sessionsUsedByPrincipal.remove(sessionId);
			if (sessionsUsedByPrincipal.isEmpty()) {
				// No need to keep object in principals Map anymore
				sessionsUsedByPrincipal = null;
			}
			return sessionsUsedByPrincipal;
		});
		LOG.info("after removal");
	}
}
