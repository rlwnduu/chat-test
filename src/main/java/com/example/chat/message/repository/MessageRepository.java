package com.example.chat.message.repository;

import com.example.chat.message.domain.Message;
import com.example.chat.message.dto.MessageView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, Long>, MessageRepositoryCustom{

    // 특정 시간 이후에 생성된 메시지 개수 카운트
    long countByChannelIdAndCreatedAtAfter(Long channelId, Instant lastReadAt);

    // 특정 채널의 가장 마지막 메시지 1개 조회
    Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(Long channelId);

    Slice<MessageView> findByChannelIdAndIdLessThan(
            Long channelId,
            Long cursor,
            Pageable pageable
    );
}
