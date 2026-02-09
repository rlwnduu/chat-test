package com.example.chat.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public String getValues(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void setHashValues(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public String getHashValues(String key, String hashKey) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        return value != null ? value.toString() : null;
    }

    public void deleteHashValues(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public List<String> getValuesList(List<String> keys) {
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);

        if (values == null) {
            return Collections.emptyList();
        }

        return values.stream()
                .map(value -> value != null ? value.toString() : null)
                .toList();
    }
}
