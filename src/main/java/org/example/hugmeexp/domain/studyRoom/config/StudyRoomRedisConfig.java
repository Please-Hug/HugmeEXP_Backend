package org.example.hugmeexp.domain.studyRoom.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * StudyRoom 도메인의 Geo/Trie 전용 Redis 설정
 *
 * 사용 범위:
 * - RedisGeoService (위치 기반 검색)
 * - RedisAutoCompleteService (자동완성)
 */

@Configuration
public class StudyRoomRedisConfig {

    /**
     * Geo 검색 전용 RedisTemplate
     * - 위치 데이터와 좌표 직렬화에 최적화
     * - RedisGeoService에서만 사용
     */
    @Bean("geoRedisTemplate")
    public RedisTemplate<String, Object> geoRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Geo 데이터 처리용 ObjectMapper (LocalDateTime 최적화)
        ObjectMapper geoObjectMapper = createGeoOptimizedObjectMapper();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(geoObjectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Trie 자동완성 전용 StringRedisTemplate
     * - 검색어와 자동완성 데이터만 처리
     * - RedisAutoCompleteService에서만 사용
     */
    @Bean("trieStringRedisTemplate")
    public StringRedisTemplate trieStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * Geo 데이터 처리에 최적화된 ObjectMapper
     * - LocalDateTime을 ISO 문자열로 직렬화
     * - 위치 데이터 처리에 특화
     */
    private ObjectMapper createGeoOptimizedObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // JavaTime 모듈 등록 (LocalDateTime 처리)
        objectMapper.registerModule(new JavaTimeModule());

        // 날짜를 타임스탬프가 아닌 ISO 문자열로 직렬화
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 다형성 타입 정보 활성화 (좌표 데이터 처리용)
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        return objectMapper;
    }
}