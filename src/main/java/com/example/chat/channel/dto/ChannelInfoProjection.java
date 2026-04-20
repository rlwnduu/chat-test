package com.example.chat.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelInfoProjection {
    private Long channelId;
    private String channelName;
    private int memberCount;
}
