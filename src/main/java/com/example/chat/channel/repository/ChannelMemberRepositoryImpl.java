package com.example.chat.channel.repository;

import com.example.chat.channel.dto.ChannelMemberProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.chat.channel.domain.QChannelMember.channelMember;
import static com.example.chat.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class ChannelMemberRepositoryImpl implements ChannelMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ChannelMemberProjection> findMembersByCursor(
            Long channelId,
            String lastNickname,
            Long lastId,
            Pageable pageable
    ) {
        List<ChannelMemberProjection> content = queryFactory
                .select(Projections.constructor(ChannelMemberProjection.class,
                        user.id,
                        user.username,
                        user.nickname,
                        user.profileImageUrl,
                        user.profileIconColor
                ))
                .from(channelMember)
                .join(channelMember.user, user)
                .where(
                        channelMember.channel.id.eq(channelId),
                        cursorCondition(lastNickname, lastId)
                )
                .orderBy(user.nickname.asc(), channelMember.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, content);
    }

    private BooleanExpression cursorCondition(String lastNickname, Long lastId) {
        if (lastNickname == null || lastId == null) {
            return null;
        }

        return user.nickname.gt(lastNickname)
                .or(user.nickname.eq(lastNickname).and(channelMember.id.gt(lastId)));
    }

    private <T> Slice<T> checkLastPage(Pageable pageable, List<T> results) {
        boolean hasNext = false;

        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}