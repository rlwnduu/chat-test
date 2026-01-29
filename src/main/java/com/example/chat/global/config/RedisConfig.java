package com.example.chat.global.config;

import com.example.chat.global.redis.RedisStreamListener;
import com.example.chat.global.redis.RedisStreamService;
import com.example.chat.message.dto.MessageEventPayload;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, MessageEventPayload>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisStreamListener streamListener) {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, MessageEventPayload>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofMillis(100)) // 0.1초마다 폴링
                        .targetType(MessageEventPayload.class) // Payload 타입 지정
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, MessageEventPayload>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        // 글로벌 스트림 키를 구독 (가장 최신 메시지부터 받기)
        Subscription subscription = container.receive(
                StreamOffset.latest(RedisStreamService.GLOBAL_STREAM_KEY),
                streamListener
        );

        container.start();
        return container;
    }
}
