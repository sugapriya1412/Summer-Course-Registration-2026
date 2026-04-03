package org.vtop.CourseRegistration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.vtop.CourseRegistration.service.LoginSessionDetailsService;

@Component
public class CustomSessionManager {

        private static final Logger LOG = LoggerFactory.getLogger(CustomSessionManager.class);

        private final String hashReference1 = "PRINCIPALDATA";

        private HashOperations<String, String, ConcurrentMap<String, List<SessionInfo>>> principalOperations;

        private RedisTemplate<String, ConcurrentMap<String, List<SessionInfo>>> sessionInfoTemplate;

        @Autowired
        private LoginSessionDetailsService sessionRepository;


        @Autowired
        public CustomSessionManager(
                        RedisTemplate<String, ConcurrentMap<String, List<SessionInfo>>> sessionInfoTemplate) {
                this.sessionInfoTemplate = sessionInfoTemplate;
        }

        public void validateLogin(HttpSession session,HttpServletRequest request,HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
                
                String principalName=authentication.getName();

                LOG.info("authentication name={}",principalName);

                this.principalOperations = sessionInfoTemplate.opsForHash();
                ConcurrentMap<String, List<SessionInfo>> principalInfo = principalOperations.get(hashReference1,principalName);
                List<SessionInfo> infoList = null;
                boolean found = false;
                if (principalInfo != null) {
                        infoList = principalInfo.get(principalName);
                        if (infoList != null && !infoList.isEmpty()) {
                                for (SessionInfo info : infoList) {
                                        if (info.getSessionId().equals(session.getId())) {
                                                if (info.isExpired()) {
                                                	sessionRepository.closeLogin(session.getId());
                                                        new SecurityContextLogoutHandler().logout(request,
                                                                        response, authentication);
                                                        authentication.setAuthenticated(false);
                                                        RequestDispatcher dispatcher = request
                                                                        .getRequestDispatcher("/duplicate/sessionError");
                                                        dispatcher.forward(request, response);
                                                        return;
                                                } else {
                                                        found = true;
                                                }
                                        } else {
                                                info.setExpired(true);
                                        }
                                }
                        } else {
                                infoList = new ArrayList<SessionInfo>();
                        }

                } else {
                        principalInfo = new ConcurrentHashMap<>();
                        infoList = new ArrayList<SessionInfo>();
                }

                if (!found) {
                        infoList.add(new SessionInfo(principalName, session.getId(), LocalDateTime.now(),
                                        false));
                        principalInfo.put(principalName, infoList);
                        principalOperations.put(hashReference1, principalName, principalInfo);
                        sessionRepository.saveLogin(session.getId(), principalName, getRemoteAddress(request));
                }
        }



        public String getRemoteAddress(HttpServletRequest request) {
                String remoteAddr = request.getHeader("X-FORWARDED-FOR");
                if (remoteAddr == null || "".equals(remoteAddr)) {
                        remoteAddr = request.getRemoteAddr();
                }

                return remoteAddr;
        }
}
