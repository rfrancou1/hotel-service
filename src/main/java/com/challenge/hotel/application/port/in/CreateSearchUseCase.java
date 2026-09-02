package com.challenge.hotel.application.port.in;

import com.challenge.hotel.application.model.SearchCommand;

import java.util.UUID;

public interface CreateSearchUseCase {

    UUID create(SearchCommand command);
}