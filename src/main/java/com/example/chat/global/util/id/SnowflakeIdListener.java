package com.example.chat.global.util.id;

import com.example.chat.global.error.BusinessException;
import com.example.chat.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnowflakeIdListener extends AbstractMongoEventListener<Object> {

    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object entity = event.getSource();
        ReflectionUtils.doWithFields(entity.getClass(), (Field field) -> {
            if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(SnowflakeId.class)) {
                try {
                    field.setAccessible(true);
                    Object idValue = ReflectionUtils.getField(field, entity);

                    if (field.getType().equals(Long.class) && idValue == null) {
                        long nextId = snowflakeIdGenerator.nextId();
                        ReflectionUtils.setField(field, entity, nextId);
                    }
                } catch (Exception e) {
                    log.error("Snowflake ID 생성 중 치명적 오류 발생 entity={}", entity.getClass().getSimpleName(), e);
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        });
    }
}
