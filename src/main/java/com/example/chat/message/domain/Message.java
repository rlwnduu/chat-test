package com.example.chat.message.domain;

import com.example.chat.global.util.id.SnowflakeId;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Document
@CompoundIndexes({
        @CompoundIndex(name = "idx_channel_id", def = "{'channelId': 1, '_id': -1}")
})
public class Message {

    @Id
    @SnowflakeId
    private Long id;

    private Long channelId;

    private Long authorId;

    private String content;

    private Instant createdAt;

    public Message() {
        createdAt = Instant.now();
    }

    public static Message create(Long channelId, Long authorId, String content) {
        Message message = new Message();
        message.channelId = channelId;
        message.authorId = authorId;
        message.content = content;
        return message;
    }
}
