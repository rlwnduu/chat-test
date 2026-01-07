package com.example.chat.global.util.id;

import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private static final long EPOCH = 1622505600000L; // 기준 시각(커스텀 가능)
    private static final long NODE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private long nodeId;

    private long lastTimestamp = -1L;
    private long sequence = 0L;


    public SnowflakeIdGenerator(@Value("${snowflake.node-id}") long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(String.format("NodeId must be between 0 and %d", MAX_NODE_ID));
        }
        this.nodeId = nodeId;
    }

    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp < lastTimestamp) {
            log.error("Clock moved backwards. Refusing to generate id");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                while (currentTimestamp <= lastTimestamp) {
                    currentTimestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = currentTimestamp;
        return ((currentTimestamp - EPOCH) << (NODE_ID_BITS + SEQUENCE_BITS))
                | (nodeId << SEQUENCE_BITS)
                | sequence;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return nextId();
    }
}
