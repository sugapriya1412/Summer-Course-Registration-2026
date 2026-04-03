package org.vtop.CourseRegistration;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vtop.CourseRegistration.model.LoginSessionDetail;
import org.vtop.CourseRegistration.repository.LoginSessionDetailRepository;


//@WebListener
public class CustomSessionListener implements HttpSessionListener {

        private static final Logger LOG = LoggerFactory.getLogger(CustomSessionListener.class);

        @Autowired
        private LoginSessionDetailRepository sessionRepository;

        @Autowired
        private HttpServletRequest request;

        @Override
        public void sessionCreated(final HttpSessionEvent event) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                LOG.info("session created");
                LoginSessionDetail data = new LoginSessionDetail();

                data.setSessionId(event.getSession().getId());
                data.setApplicationName("Course registration");
                data.setPrincipalName(authentication.getName());
                data.setLogIpaddress(getRemoteAddress(request));
                data.setSessionCreatedTime(LocalDateTime.now());
                data.setRemarks("-");

                sessionRepository.save(data);
        }

        @Override
        public void sessionDestroyed(final HttpSessionEvent event) {

                Optional<LoginSessionDetail> data = sessionRepository.findById(event.getSession().getId());

                if (data.isPresent()) {
                        data.get().setSessionClosedTime(LocalDateTime.now());
                        sessionRepository.save(data.get());
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
