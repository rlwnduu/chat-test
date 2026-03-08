package com.example.chat.message.repository;

import com.example.chat.message.domain.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Map<Long, Message> findLastMessagesByChannelIds(List<Long> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) {
            return Map.of();
        }

        MatchOperation match = Aggregation.match(Criteria.where("channelId").in(channelIds));

        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "_id");

        GroupOperation group = Aggregation.group("channelId")
                .first(Aggregation.ROOT).as("lastMessage");

        Aggregation aggregation = Aggregation.newAggregation(match, sort, group);
        AggregationResults<ChannelLastMessageAggregationResult> results = mongoTemplate.aggregate(
                aggregation, "message", ChannelLastMessageAggregationResult.class
        );

        return results.getMappedResults().stream()
                .collect(Collectors.toMap(
                        ChannelLastMessageAggregationResult::getChannelId,
                        ChannelLastMessageAggregationResult::getLastMessage
                ));
    }

    @Getter
    private static class ChannelLastMessageAggregationResult {
        @Id
        private Long channelId;
        private Message lastMessage;
    }

    @Override
    public long countUnreadMessages(Long channelId, Long lastReadMessageId, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("channelId").is(channelId)
                .and("_id").gt(lastReadMessageId));
        query.limit(limit);
        query.fields().include("_id");
        return mongoTemplate.find(query, Message.class).size();
    }

    @Override
    public Map<Long, Integer> countUnreadMessagesBatch(Map<Long, Long> channelReadMap) {
        if (channelReadMap.isEmpty()) {
            return new HashMap<>();
        }

        List<Criteria> criteriaList = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : channelReadMap.entrySet()) {
            Long channelId = entry.getKey();
            Long lastReadMessageId = entry.getValue();
            long readId = (lastReadMessageId != null) ? lastReadMessageId : 0L;

            criteriaList.add(
                    Criteria.where("channelId").is(channelId)
                            .and("_id").gt(readId)
            );
        }
        Criteria matchCriteria = new Criteria().orOperator(criteriaList);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                Aggregation.group("channelId").count().as("count")
        );

        AggregationResults<UnreadCountResult> results = mongoTemplate.aggregate(
                aggregation,
                "message",
                UnreadCountResult.class
        );

        Map<Long, Integer> resultMap = new HashMap<>();
        for (UnreadCountResult result : results.getMappedResults()) {
            resultMap.put(result.getChannelId(), result.getCount());
        }

        return resultMap;
    }

    @Getter
    public static class UnreadCountResult {

        @Id
        private Long channelId;
        private Integer count;
    }
}
