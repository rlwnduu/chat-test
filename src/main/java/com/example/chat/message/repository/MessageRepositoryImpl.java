package com.example.chat.message.repository;

import com.example.chat.message.domain.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public long countUnreadMessages(Long channelId, Long lastReadMessageId, int limit) {
        // 1. 쿼리 조건 생성: channelId가 같고, ID가 lastReadId보다 큰 것
        Query query = new Query();
        query.addCriteria(Criteria.where("channelId").is(channelId)
                .and("_id").gt(lastReadMessageId));

        // 2. Limit 설정 (예: 300개까지만)
        query.limit(limit);

        // 3. 최적화: 실제 데이터를 다 가져오지 않고 '_id'만 가져오도록 필드 제한 (Covering Index 활용)
        query.fields().include("_id");

        // 4. 실행: count() 메서드는 limit을 무시하는 경우가 많으므로,
        // find()로 가져온 리스트의 사이즈를 반환하는 것이 가장 확실하고 빠름 (인덱스만 탐색함)
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
