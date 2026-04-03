package org.vtop.CourseRegistration;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class SessionEventListener  extends HttpSessionEventPublisher  {
 
        private static final Logger LOG = LoggerFactory.getLogger(SessionEventListener.class);

        @Override
        public void sessionDestroyed(HttpSessionEvent event) {

                LOG.info("you have entered into session destroy event");
            String name = null;
            SessionRegistry sessionRegistry = getSessionRegistry(event);
            SessionInformation sessionInfo = (sessionRegistry != null ? sessionRegistry
                .getSessionInformation(event.getSession().getId()) : null);
       
            super.sessionDestroyed(event);
        }


        public SessionRegistry getSessionRegistry(HttpSessionEvent event) {
                HttpSession session = event.getSession();
                ApplicationContext ctx =
                    WebApplicationContextUtils.
                            getWebApplicationContext(session.getServletContext());
                return (SessionRegistry) ctx.getBean("sessionRegistry");
            }
}
