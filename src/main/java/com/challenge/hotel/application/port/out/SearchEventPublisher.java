package com.challenge.hotel.application.port.out;

import com.challenge.hotel.domain.model.HotelSearch;

public interface SearchEventPublisher {

    void publish(HotelSearch search);
}