package com.saytech.expentrack.notificationservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void listen(String message) {
        System.out.println("Received notification: " + message);
    }
}
