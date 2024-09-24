package com.saytech.expentrack.notificationservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saytech.expentrack.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;


    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void listen(String message) {
        logger.info("Received notification: {}", message);
        try {
            JsonNode eventNode = objectMapper.readTree(message);
            processEvent(eventNode);
        } catch (Exception e) {
            logger.error("Failed to process message: {}", message, e);
        }
    }

    private void processEvent(JsonNode eventNode) {
        String eventType = eventNode.get("type").asText();
        Long userId = eventNode.get("userId").asLong();
        switch (eventType) {
            case "BudgetExceeded":
                handleBudgetExceededEvent(userId);
                break;
            case "ExpenseEvent":
                handleExpenseEvent(userId);
                break;
            default:
                logger.warn("Unknown notification type: {}", eventType);
        }
    }

    private void handleBudgetExceededEvent(Long userId) {
        String notificationMessage = "You have exceeded your budget!";
        notificationService.sendNotification(userId, notificationMessage);
        logger.info("Handled BudgetExceeded event for user: {}", userId);
    }

    private void handleExpenseEvent(Long userId) {
        String notificationMessage = "New expense recorded.";
        notificationService.sendNotification(userId, notificationMessage);
        logger.info("Handled ExpenseEvent for user: {}", userId);
    }

}
