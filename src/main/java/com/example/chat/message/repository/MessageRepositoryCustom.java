package com.example.chat.message.repository;

import com.example.chat.message.domain.Message;

import java.util.List;
import java.util.Map;

public interface MessageRepositoryCustom {

    Map<Long, Message> findLastMessagesByChannelIds(List<Long> channelIds);

    long countUnreadMessages(Long channelId, Long lastReadMessageId, int limit);

    Map<Long, Integer> countUnreadMessagesBatch(Map<Long, Long> channelReadMap);
}