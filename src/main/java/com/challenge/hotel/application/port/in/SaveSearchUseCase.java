package com.challenge.hotel.application.port.in;

import com.challenge.hotel.domain.model.HotelSearch;

public interface SaveSearchUseCase {

    void save(HotelSearch search);
}