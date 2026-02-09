package com.example.chat.channel.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ChannelSummaryProjection {

    private final Long channelId;
    private final String channelName;
    private final int memberCount;
    private final Long lastMessageId;
    private final String lastMessageContent;
    private final Instant lastMessageAt;
    private final Long myLastReadMessageId;

    public ChannelSummaryProjection(
            Long channelId,
            String channelName,
            int memberCount,
            Long lastMessageId,
            String lastMessageContent,
            Instant lastMessageAt,
            Long myLastReadMessageId
    ) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.memberCount = memberCount;
        this.lastMessageId = lastMessageId;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageAt = lastMessageAt;
        this.myLastReadMessageId = myLastReadMessageId;
    }
}
