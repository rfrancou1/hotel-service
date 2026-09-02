package com.challenge.hotel.infrastructure.kafka.consumer;

import com.challenge.hotel.application.port.in.SaveSearchUseCase;
import com.challenge.hotel.domain.model.HotelSearch;
import com.challenge.hotel.infrastructure.kafka.model.SearchEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaSearchConsumer {

    private final SaveSearchUseCase saveSearchUseCase;

    public KafkaSearchConsumer(
            SaveSearchUseCase saveSearchUseCase
    ) {
        this.saveSearchUseCase = saveSearchUseCase;
    }

    @KafkaListener(
            topics = "hotel_availability_searches",
            groupId = "hotel-service"
    )
    public void consume(SearchEvent event) {

        HotelSearch search = new HotelSearch(
                event.searchId(),
                event.hotelId(),
                event.checkIn(),
                event.checkOut(),
                event.ages()
        );

        saveSearchUseCase.save(search);
    }
}