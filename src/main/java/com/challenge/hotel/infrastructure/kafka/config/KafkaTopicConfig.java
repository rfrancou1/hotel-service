package com.challenge.hotel.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic hotelAvailabilitySearchesTopic() {
        return TopicBuilder
                .name("hotel_availability_searches")
                .partitions(3)
                .replicas(1)
                .build();
    }
}