package com.example.chat.message.repository;

import java.util.Map;

public interface MessageRepositoryCustom {

    long countUnreadMessages(Long channelId, Long lastReadMessageId, int limit);

    Map<Long, Integer> countUnreadMessagesBatch(Map<Long, Long> channelReadMap);
}