package org.vtop.CourseRegistration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.SessionInformation;


public class RedisReadWriteTemplate {
        
        private static final Logger LOG = LoggerFactory.getLogger(RedisReadWriteTemplate.class);

        private  RedisTemplate<String, ConcurrentMap<Object, Set<String>>> principalTemplate;
        private  RedisTemplate<String, Map<String, SessionInformation>> sessionIdTemplate;
        private RedisTemplate<String, ConcurrentMap<Object, Set<SessionInfo>>> sessionInfoTemplate;

        private HashOperations<String, String, ConcurrentMap<Object, Set<String>>> principalOperations;
        private HashOperations<String, String, Map<String, SessionInformation>> sessionOperations;



        public RedisReadWriteTemplate(RedisTemplate<String, ConcurrentMap<Object, Set<SessionInfo>>> sessionInfoTemplate) {  //RedisTemplate<String, ConcurrentMap<Object, Set<String>>> principalTemplate,RedisTemplate<String, Map<String, SessionInformation>> sessionIdTemplate
                this.sessionInfoTemplate=sessionInfoTemplate;
        }
        public RedisTemplate<String, ConcurrentMap<Object, Set<String>>> getPrincipalTemplate() {
                return principalTemplate;
        }


        public RedisTemplate<String, Map<String, SessionInformation>> getSessionIdTemplate() {
                return sessionIdTemplate;
        }
        
        public RedisTemplate<String, ConcurrentMap<Object, Set<SessionInfo>>> getSessionInfoTemplate() {
                return sessionInfoTemplate;
        }

        public void setSessionInfoTemplate(RedisTemplate<String, ConcurrentMap<Object, Set<SessionInfo>>> sessionInfoTemplate) {
                this.sessionInfoTemplate = sessionInfoTemplate;
        }





}
