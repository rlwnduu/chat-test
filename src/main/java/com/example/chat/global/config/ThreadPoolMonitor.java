package com.example.chat.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ThreadPoolMonitor {

    // 스프링의 모든 빈을 관리하는 거대한 컨테이너를 직접 가져옵니다.
    private final ApplicationContext applicationContext;

//    @Scheduled(fixedRate = 1000)
//    public void logThreadPoolStatus() {
//        // "컨테이너야, ThreadPoolTaskExecutor 타입인 빈은 싹 다 가져와 봐"
//        Map<String, ThreadPoolTaskExecutor> executors = applicationContext.getBeansOfType(ThreadPoolTaskExecutor.class);
//
//        log.info("====== Thread Pool Status (Total: {}) ======", executors.size());
//
//        for (Map.Entry<String, ThreadPoolTaskExecutor> entry : executors.entrySet()) {
//            String name = entry.getKey();
//            ThreadPoolTaskExecutor executor = entry.getValue();
//
//            int active = executor.getActiveCount();
//            int max = executor.getMaxPoolSize();
//            int queueSize = executor.getThreadPoolExecutor().getQueue().size();
//
//            log.info("🎯 [Monitor] Bean: {}, Active={}/{}, Queue={}", name, active, max, queueSize);
//
//            // [경고] Inbound 채널(메시지 들어오는 곳)이 꽉 찼는지 확인
//            if (name.contains("Inbound") && queueSize > 50) {
//                log.error("🚨 Inbound 대기열 위험 수준! (Queue: {})", queueSize);
//            }
//        }
//        log.info("===========================================");
//    }
}
