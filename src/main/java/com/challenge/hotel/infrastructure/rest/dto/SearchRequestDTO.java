package com.challenge.hotel.infrastructure.rest.dto;

import com.challenge.hotel.infrastructure.rest.validation.ValidDateRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

@ValidDateRange
public record SearchRequestDTO(

        @NotBlank(message = "hotelId must not be blank")
        String hotelId,

        @NotNull(message = "checkIn must not be null")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @NotNull(message = "checkOut must not be null")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        @NotNull(message = "ages must not be null")
        @NotEmpty(message = "ages must not be empty")
        List<@NotNull @PositiveOrZero Integer> ages
) {

    public SearchRequestDTO {
        ages = ages == null ? null : List.copyOf(ages);
    }
}