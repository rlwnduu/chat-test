package com.example.chat.channel.dto;

public interface ChannelSummaryProjection {
    Long getId();
    String getChannelName();
    int getMemberCount();
    Long getLastReadMessageId();
}
