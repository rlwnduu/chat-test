package com.example.chat.invitation.repository;

import com.example.chat.invitation.domain.RequestStatus;
import com.example.chat.invitation.dto.ChannelInviteResponse;
import com.example.chat.invitation.dto.InviteSearchCondition;
import com.example.chat.user.domain.QUser;
import com.example.chat.user.dto.UserInfoResponse;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.chat.channel.domain.QChannel.channel;
import static com.example.chat.invitation.domain.QChannelInvite.channelInvite;

@RequiredArgsConstructor
public class ChannelInviteRepositoryImpl implements ChannelInviteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChannelInviteResponse> search(InviteSearchCondition condition) {
        QUser inviter = new QUser("inviter");
        QUser invitee = new QUser("invitee");

        return queryFactory
                .select(Projections.constructor(ChannelInviteResponse.class,
                        channelInvite.id,
                        channel.id,
                        channel.channelName,
                        userInfoProjection(inviter),
                        userInfoProjection(invitee),
                        channelInvite.status,
                        channelInvite.createdAt
                ))
                .from(channelInvite)
                .join(channelInvite.channel, channel)
                .join(channelInvite.inviter, inviter)
                .join(channelInvite.invitee, invitee)
                .where(
                        channelInvite.invitee.id.eq(condition.getUserId()),
                        eqStatus(condition.getStatus()),
                        ltCursorId(condition.getCursorId())
                )
                .orderBy(channelInvite.id.desc())
                .limit(condition.getSize() + 1)
                .fetch();
    }

    private FactoryExpression<UserInfoResponse> userInfoProjection(QUser user) {
        return Projections.constructor(UserInfoResponse.class,
                user.id,
                user.username,
                user.nickname,
                user.profileImageUrl,
                user.profileIconColor
        );
    }

    private BooleanExpression eqStatus(RequestStatus status) {
        return status != null ? channelInvite.status.eq(status) : null;
    }

    private BooleanExpression ltCursorId(Long cursorId) {
        return cursorId == null ? null : channelInvite.id.lt(cursorId);
    }
}
