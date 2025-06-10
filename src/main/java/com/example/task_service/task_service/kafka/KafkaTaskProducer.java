package com.example.task_service.task_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaTaskProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTaskEvent(Object eventPayload) {
        kafkaTemplate.executeInTransaction(kt -> {
            log.info("Sending task event to Kafka: {}", eventPayload);
            kt.send("task-events", eventPayload);
            return true;
        });
    }
}
