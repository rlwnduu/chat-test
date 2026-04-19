package com.example.chat.message.mapper;

import com.example.chat.message.domain.Message;
import com.example.chat.message.dto.MessageEventPayload;
import com.example.chat.message.dto.MessageResponse;
import com.example.chat.message.dto.MessageView;
import com.example.chat.user.dto.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    MessageResponse toResponse(Message message);

    MessageResponse toResponse(MessageView messageView);

    List<MessageResponse> toResponseList(List<MessageView> messageViews);

    MessageEventPayload toEventPayload(MessageResponse message, UserInfoResponse sender);
}
