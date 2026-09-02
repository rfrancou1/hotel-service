package com.challenge.hotel.domain.exception;

import java.util.UUID;

public class SearchNotFoundException extends RuntimeException {

    public SearchNotFoundException(UUID searchId) {
        super("Search not found: %s".formatted(searchId));
    }
}