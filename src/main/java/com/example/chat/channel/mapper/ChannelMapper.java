package com.example.chat.channel.mapper;

import com.example.chat.channel.domain.Channel;
import com.example.chat.channel.dto.ChannelCreateRequest;
import com.example.chat.channel.dto.ChannelMemberProjection;
import com.example.chat.channel.dto.ChannelMemberResponse;
import com.example.chat.channel.dto.ChannelSummaryProjection;
import com.example.chat.channel.dto.ChannelSummaryResponse;
import com.example.chat.message.domain.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChannelMapper {

    Channel toEntity(ChannelCreateRequest request);

    @Mapping(target = "channelId", source = "proj.id")
    @Mapping(target = "lastMessageId", source = "lastMessage.id")
    @Mapping(target = "lastMessageContent", source = "lastMessage.content")
    @Mapping(target = "lastMessageAt", source = "lastMessage.createdAt")
    ChannelSummaryResponse toSummaryResponse(ChannelSummaryProjection proj, Message lastMessage, int unreadCount);

    @Mapping(target = "channelId", source = "channel.id")
    @Mapping(target = "lastMessageId", source = "lastMessage.id")
    @Mapping(target = "lastMessageContent", source = "lastMessage.content")
    @Mapping(target = "lastMessageAt", source = "lastMessage.createdAt")
    ChannelSummaryResponse toSummaryResponse(Channel channel, Message lastMessage, int unreadCount);

    ChannelMemberResponse toMemberResponse(ChannelMemberProjection projection);
}
