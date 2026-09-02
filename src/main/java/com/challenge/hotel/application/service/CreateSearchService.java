package com.challenge.hotel.application.service;

import com.challenge.hotel.application.model.SearchCommand;
import com.challenge.hotel.application.port.in.CreateSearchUseCase;
import com.challenge.hotel.application.port.out.SearchEventPublisher;
import com.challenge.hotel.domain.model.HotelSearch;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class CreateSearchService implements CreateSearchUseCase {

    private final SearchEventPublisher eventPublisher;

    public CreateSearchService(SearchEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public UUID create(SearchCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        UUID searchId = UUID.randomUUID();

        HotelSearch search = new HotelSearch(
                searchId,
                command.hotelId(),
                command.checkIn(),
                command.checkOut(),
                command.ages()
        );

        eventPublisher.publish(search);

        return searchId;
    }
}