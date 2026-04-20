package com.example.chat.user.repository;

import com.example.chat.user.dto.UserInfoProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static com.example.chat.user.domain.QUser.user;
import static com.example.chat.user.domain.QUserFriend.userFriend;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserInfoProjection> findUserInfoByIdCustom(Long userId) {
        UserInfoProjection result = queryFactory
                .select(Projections.constructor(UserInfoProjection.class,
                        user.id.stringValue(),
                        user.username,
                        user.nickname,
                        user.profileImageUrl,
                        user.profileIconColor
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Slice<UserInfoProjection> findFriendsByUserIdWithCursorCustom(Long userId, Long cursor, Pageable pageable) {
        List<UserInfoProjection> content = queryFactory
                .select(Projections.constructor(UserInfoProjection.class,
                        user.id.stringValue(),
                        user.username,
                        user.nickname,
                        user.profileImageUrl,
                        user.profileIconColor
                ))
                .from(user)
                .where(
                        isFriend(userId),
                        ltCursorId(cursor)
                )
                .orderBy(user.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression isFriend(Long userId) {
        return user.id.in(
                JPAExpressions.select(userFriend.userB.id)
                        .from(userFriend)
                        .where(userFriend.userA.id.eq(userId))
        ).or(
                user.id.in(
                        JPAExpressions.select(userFriend.userA.id)
                                .from(userFriend)
                                .where(userFriend.userB.id.eq(userId))
                )
        );
    }

    private BooleanExpression ltCursorId(Long cursor) {
        return cursor != null ? user.id.lt(cursor) : null;
    }
}
