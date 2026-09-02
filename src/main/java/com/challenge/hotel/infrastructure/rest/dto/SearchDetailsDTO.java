package com.challenge.hotel.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record SearchDetailsDTO(

        String hotelId,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        List<Integer> ages
) {

    public SearchDetailsDTO {
        ages = List.copyOf(ages);
    }
}