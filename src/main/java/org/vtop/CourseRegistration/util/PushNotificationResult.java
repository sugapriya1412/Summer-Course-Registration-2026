package org.vtop.CourseRegistration.util;

public class PushNotificationResult {
       
        int responseCode;
        String message;
        String additionalMessage;
        
        public PushNotificationResult() {
                this.responseCode=0;
                this.message="";
                this.additionalMessage="";
        }

        public PushNotificationResult(int responseCode, String message) {
                this.responseCode = responseCode;
                this.message = message;
                this.additionalMessage="";
        }

        public PushNotificationResult(int responseCode, String message, String additionalMessage) {
                this.responseCode = responseCode;
                this.message = message;
                this.additionalMessage = additionalMessage;
        }
        
        public int getResponseCode() {
                return responseCode;
        }
        public void setResponseCode(int responseCode) {
                this.responseCode = responseCode;
        }
        public String getMessage() {
                return message;
        }
        public void setMessage(String message) {
                this.message = message;
        }
        public String getAdditionalMessage() {
                return additionalMessage;
        }
        public void setAdditionalMessage(String additionalMessage) {
                this.additionalMessage = additionalMessage;
        }


        
}
