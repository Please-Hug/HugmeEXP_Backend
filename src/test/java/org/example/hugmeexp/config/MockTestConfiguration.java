package org.example.hugmeexp.config;

import com.amazonaws.services.s3.AmazonS3Client;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@TestConfiguration
public class MockTestConfiguration {

    @Bean
    @Primary
    public AmazonS3Client amazonS3Client() {
        return Mockito.mock(AmazonS3Client.class);
    }

    @Bean
    @Primary
        public RedisTemplate<String, String> stringRedisTemplate() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> redisTemplate = Mockito.mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        return redisTemplate;
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> objectRedisTemplate() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> redisTemplate = Mockito.mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        return redisTemplate;
    }
}
