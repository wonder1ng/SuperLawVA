package com.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    /**
     * 🟢 Redis 연결 팩토리 - 메인 Redis 연결 설정
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);

        // 비밀번호가 설정되어 있는 경우에만 적용
        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.setPassword(redisPassword);
        }

        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        log.info("Redis 연결 설정 완료: {}:{}", redisHost, redisPort);
        return factory;
    }

    /**
     * 🟢 String 전용 Redis 템플릿 (이메일 인증 코드 저장용)
     * RedisEmailService에서 사용
     */
    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        log.info("StringRedisTemplate 설정 완료");
        return template;
    }

    /**
     * 🟢 범용 Redis 템플릿 (향후 확장용)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key와 Value에 String 직렬화 사용
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        log.info("RedisTemplate 설정 완료");
        return template;
    }
}

/**
 * 🚨 왜 필요한가?
 *
 * 1. Redis 연결 설정: application.yml의 Redis 설정을 실제 Bean으로 생성
 * 2. StringRedisTemplate: RedisEmailService가 의존성 주입받는 핵심 Bean
 * 3. 환경별 설정: 로컬/테스트/운영 환경에 맞는 Redis 연결
 * 4. 없으면 오류: "Could not autowire. No beans of 'StringRedisTemplate' type found"
 */