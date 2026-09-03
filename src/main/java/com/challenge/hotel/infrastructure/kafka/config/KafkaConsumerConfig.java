package com.challenge.hotel.infrastructure.kafka.config;

import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<Object, Object>
    kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory
    ) {
        var factory =
                new ConcurrentKafkaListenerContainerFactory<Object, Object>();

        configurer.configure(factory, consumerFactory);

        var executor =
                new SimpleAsyncTaskExecutor("kafka-vt-");

        executor.setVirtualThreads(true);

        factory.getContainerProperties()
                .setListenerTaskExecutor(executor);

        return factory;
    }
}
