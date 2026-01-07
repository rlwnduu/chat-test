package com.example.chat.global.config;

import com.example.chat.global.util.id.SnowflakeIdGenerator;
import com.example.chat.global.util.id.SnowflakeIdListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public SnowflakeIdListener snowflakeIdListener(SnowflakeIdGenerator snowflakeIdGenerator) {
        return new SnowflakeIdListener(snowflakeIdGenerator);
    }
}
