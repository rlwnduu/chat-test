package com.example.chat.invitation.repository;

import com.example.chat.invitation.domain.InvitationStatus;
import com.example.chat.invitation.dto.FriendRequestProjection;
import com.example.chat.invitation.dto.InviteSearchCondition;
import com.example.chat.user.domain.QUser;
import com.example.chat.user.dto.UserInfoProjection;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.chat.invitation.domain.QFriendRequest.friendRequest;

@RequiredArgsConstructor
public class FriendRequestRepositoryImpl implements FriendRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FriendRequestProjection> search(InviteSearchCondition condition) {
        QUser inviter = new QUser("inviter");
        QUser invitee = new QUser("invitee");

        return queryFactory
                .select(Projections.constructor(FriendRequestProjection.class,
                        friendRequest.id,
                        userInfoProjection(inviter),
                        userInfoProjection(invitee),
                        friendRequest.status,
                        friendRequest.createdAt
                ))
                .from(friendRequest)
                .join(friendRequest.inviter, inviter)
                .join(friendRequest.invitee, invitee)
                .where(
                        friendRequest.invitee.id.eq(condition.getUserId()),
                        eqStatus(condition.getStatus()),
                        ltCursorId(condition.getCursorId())
                )
                .orderBy(friendRequest.id.desc())
                .limit(condition.getSize() + 1)
                .fetch();
    }

    private FactoryExpression<UserInfoProjection> userInfoProjection(QUser user) {
        return Projections.constructor(UserInfoProjection.class,
                user.id.stringValue(),
                user.username,
                user.nickname,
                user.profileImageUrl,
                user.profileIconColor
        );
    }

    private BooleanExpression eqStatus(InvitationStatus status) {
        return status != null ? friendRequest.status.eq(status) : null;
    }

    private BooleanExpression ltCursorId(Long cursorId) {
        return cursorId == null ? null : friendRequest.id.lt(cursorId);
    }
}
