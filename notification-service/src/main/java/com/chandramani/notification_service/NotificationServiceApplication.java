package com.chandramani.notification_service;

import com.chandramani.notification_service.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic", groupId = "notificationId")
	public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
		// Here we would send an actual email
		log.info("Received Notification for Order - {}", orderPlacedEvent.getOrderNumber());
	}
}