package org.vtop.CourseRegistration;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SessionInfo implements Serializable{

        private static final long serialVersionUID = -1L;


        private String principal;
        private String sessionId;
        private LocalDateTime sessionCreatedDate;
        private boolean isExpired;


        
        public SessionInfo(String principal, String sessionId, LocalDateTime sessionCreatedDate, boolean isExpired) {
                this.principal = principal;
                this.sessionId = sessionId;
                this.sessionCreatedDate = sessionCreatedDate;
                this.isExpired = isExpired;
        }
        
        public String getPrincipal() {
                return principal;
        }
        public void setPrincipal(String principal) {
                this.principal = principal;
        }
        public String getSessionId() {
                return sessionId;
        }
        public void setSessionId(String sessionId) {
                this.sessionId = sessionId;
        }
        public LocalDateTime getSessionCreatedDate() {
                return sessionCreatedDate;
        }
        public void setSessionCreatedDate(LocalDateTime sessionCreatedDate) {
                this.sessionCreatedDate = sessionCreatedDate;
        }
        public boolean isExpired() {
                return isExpired;
        }
        public void setExpired(boolean isExpired) {
                this.isExpired = isExpired;
        }

        
}
