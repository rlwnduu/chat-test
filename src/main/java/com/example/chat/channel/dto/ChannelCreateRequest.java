package com.example.chat.channel.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChannelCreateRequest {

    private String channelName;

    public ChannelCreateRequest() {
    }

    public ChannelCreateRequest(String channelName) {
        this.channelName = channelName;
    }
}
