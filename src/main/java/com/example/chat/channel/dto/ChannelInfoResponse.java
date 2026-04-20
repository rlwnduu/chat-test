package com.example.chat.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfoResponse {
    private String channelId;
    private String channelName;
    private int memberCount;
}
