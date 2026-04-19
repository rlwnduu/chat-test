package com.example.chat.channel.repository;

import com.example.chat.channel.dto.ChannelMemberProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChannelMemberRepositoryCustom {

    Slice<ChannelMemberProjection> findMembersByCursor(
            Long channelId,
            String lastNickname,
            Long lastId,
            Pageable pageable
    );
}
