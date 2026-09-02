package com.challenge.hotel.application.port.in;

import com.challenge.hotel.application.model.SearchCount;

import java.util.UUID;

public interface CountSearchUseCase {

    SearchCount getCount(UUID searchId);
}