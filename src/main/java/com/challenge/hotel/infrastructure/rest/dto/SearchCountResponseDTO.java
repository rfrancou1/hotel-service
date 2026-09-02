package com.challenge.hotel.infrastructure.rest.dto;

import java.util.UUID;

public record SearchCountResponseDTO(
        UUID searchId,
        SearchDetailsDTO search,
        long count
) {
}