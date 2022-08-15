package com.io.autocomplete.configuration;

import com.io.autocomplete.model.AutoCompleteResponse;
import com.io.autocomplete.constants.AppConstants;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.context.annotation.Bean;

/**
 * We're creating a new serialization context that uses a StringRedisSerializer for the key, a GenericToStringSerializer
 * for the value, a StringRedisSerializer for the hash key, and a GenericJackson2JsonRedisSerializer for the hash value
 */
@Configuration
public class RedisConfiguration {

    /**
     * It creates a connection to the Redis server.
     *
     * @return A connection factory for a Redis server.
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setHostName(AppConstants.REDIS_HOST);
        redisStandaloneConfig.setPort(AppConstants.REDIS_PORT);
        redisStandaloneConfig.setPassword(AppConstants.REDIS_PASSWORD);
        return new LettuceConnectionFactory(redisStandaloneConfig);
    }
    /**
     * We're creating a new serialization context that uses a StringRedisSerializer for the key, a
     * GenericToStringSerializer for the value, a StringRedisSerializer for the hash key, and a
     * GenericJackson2JsonRedisSerializer for the hash value
     *
     * @param connectionFactory The connection factory to use.
     * @return A ReactiveRedisOperations object.
     */
    @Bean
    public ReactiveRedisOperations<String, AutoCompleteResponse> redisOperations(LettuceConnectionFactory connectionFactory) {
        RedisSerializationContext<String, AutoCompleteResponse> serializationContext = RedisSerializationContext
                .<String, AutoCompleteResponse>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new GenericToStringSerializer<>(AutoCompleteResponse.class))
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
