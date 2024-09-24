package com.saytech.expentrack.notificationservice.service;

import com.saytech.expentrack.notificationservice.model.Notification;
import com.saytech.expentrack.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final String TOPIC = "notification-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(Long userId, String message) {
        // Save notification to database
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        return notification;
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public List<Notification> markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId,false);
        if (!CollectionUtils.isEmpty(notifications)) {
            List<Notification> readNotifications = notifications.stream()
                    .peek(item -> item.setRead(true))
                    .collect(Collectors.toList());
            notificationRepository.saveAll(readNotifications);
        }
        return notifications;
    }

    public void deleteAllNotificationsForUser(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }
}
