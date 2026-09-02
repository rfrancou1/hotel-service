package com.challenge.hotel.infrastructure.kafka.producer;

import com.challenge.hotel.application.port.out.SearchEventPublisher;
import com.challenge.hotel.domain.model.HotelSearch;
import com.challenge.hotel.infrastructure.kafka.model.SearchEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSearchEventPublisher implements SearchEventPublisher {

    private static final String TOPIC = "hotel_availability_searches";

    private final KafkaTemplate<String, SearchEvent> kafkaTemplate;

    public KafkaSearchEventPublisher(
            KafkaTemplate<String, SearchEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(HotelSearch search) {

        SearchEvent event = new SearchEvent(
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages()
        );

        kafkaTemplate.send(
                TOPIC,
                search.searchId().toString(),
                event
        ).join();
    }
}