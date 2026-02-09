package com.example.chat.message.repository;

import com.example.chat.message.domain.Message;
import com.example.chat.message.dto.MessageView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, Long>, MessageRepositoryCustom {

    long countByChannelIdAndCreatedAtAfter(Long channelId, Instant lastReadAt);

    Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(Long channelId);

    Slice<MessageView> findByChannelIdAndIdLessThan(
            Long channelId,
            Long cursor,
            Pageable pageable
    );
}
