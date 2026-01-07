package com.example.chat.channel.dto;

import com.example.chat.channel.domain.Channel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ChannelSummaryResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long channelId;

    private String channelName;

    private int memberCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long lastMessageId;

    private String lastMessageContent;

    private Instant lastMessageAt;

    private int unreadCount;

    public ChannelSummaryResponse(Long channelId, String channelName, int memberCount,
                                  Long lastMessageId, String lastMessageContent,
                                  Instant lastMessageAt, int unreadCount) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.memberCount = memberCount;
        this.lastMessageId = lastMessageId;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageAt = lastMessageAt;
        this.unreadCount = unreadCount;
    }

    public static ChannelSummaryResponse from(Channel channel, int unreadCount) {
        return new ChannelSummaryResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getMemberCount(),
                channel.getLastMessageId(),
                channel.getLastMessageContent(),
                channel.getLastMessageAt(),
                unreadCount
        );
    }

    public static ChannelSummaryResponse from(ChannelSummaryProjection proj, int unreadCount) {
        return new ChannelSummaryResponse(
                proj.getChannelId(),
                proj.getChannelName(),
                proj.getMemberCount(),
                proj.getLastMessageId(),
                proj.getLastMessageContent(),
                proj.getLastMessageAt(),
                unreadCount
        );
    }
}
