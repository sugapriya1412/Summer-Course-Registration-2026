package org.vtop.CourseRegistration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.security.core.session.SessionInformation;

@Configuration
public class RedisConfig {

        @Value("${redis.host}")
        private String hostName;

        @Value("${redis.port}")
        private int port;

        @Value("${redis.pool.maxIdle}")
        private int maxIdle;

        @Value("${redis.pool.minIdle}")
        private int minIdle;
        
        @Value("${redis.password}")
        private String password;

        @Value("${redis.pool.maxWaitMillis}")
        private int maxWaitMillis;

        @Value("${redis.pool.maxTotal}")
        private int maxTotal;

        @Bean
        public LettuceConnectionFactory redisConnectionFactory() {
                RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
                redisStandaloneConfiguration.setHostName(hostName);
                redisStandaloneConfiguration.setPort(port);

                //redisStandaloneConfiguration.setUsername("");
                redisStandaloneConfiguration.setPassword(password);
                
                GenericObjectPoolConfig<LettucePoolingClientConfiguration> poolConfig = new GenericObjectPoolConfig<LettucePoolingClientConfiguration>();
                poolConfig.setMaxIdle(maxIdle);
                poolConfig.setMinIdle(minIdle);
                // poolConfig.setMaxWaitMillis(maxWaitMillis);
                poolConfig.setMaxTotal(maxTotal);

                LettucePoolingClientConfiguration lettucePoolingClientConfiguration = LettucePoolingClientConfiguration
                                .builder()
                                .commandTimeout(Duration.ofSeconds(10))
                                .shutdownTimeout(Duration.ZERO)
                                .poolConfig(poolConfig)
                                .build();

                LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(
                                redisStandaloneConfiguration, lettucePoolingClientConfiguration);
                lettuceConnectionFactory.setShareNativeConnection(false);

                return lettuceConnectionFactory;
        }

        @Bean
        public RedisTemplate<String, ConcurrentMap<String, List<SessionInfo>>> sessionInfoTemplate() {
                RedisTemplate<String, ConcurrentMap<String, List<SessionInfo>>> redisTemplate = new RedisTemplate<>();
                redisTemplate.setConnectionFactory(redisConnectionFactory());
                redisTemplate.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
                return redisTemplate;
        }




        @Bean
        public RedisTemplate<String, ConcurrentMap<Object, Set<String>>> principalTemplate() {
                RedisTemplate<String, ConcurrentMap<Object, Set<String>>> redisTemplate = new RedisTemplate<>();
                redisTemplate.setConnectionFactory(redisConnectionFactory());
                redisTemplate.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
                return redisTemplate;
        }


        @Bean
        public RedisTemplate<String, Map<String, SessionInformation>> sessionIdTemplate() {
                RedisTemplate<String, Map<String, SessionInformation>> redisTemplate = new RedisTemplate<>();
                redisTemplate.setConnectionFactory(redisConnectionFactory());
                redisTemplate.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
                return redisTemplate;
        }

}
