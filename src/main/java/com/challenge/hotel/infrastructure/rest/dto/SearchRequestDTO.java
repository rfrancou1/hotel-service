package com.challenge.hotel.infrastructure.rest.dto;

import com.challenge.hotel.infrastructure.rest.validation.ValidDateRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

@ValidDateRange
public record SearchRequestDTO(

        @Schema(
                description = "Hotel identifier",
                example = "1234aBc"
        )
        @NotBlank(message = "hotelId must not be blank")
        String hotelId,

        @Schema(
                description = "Check-in date",
                example = "29/12/2026"
        )
        @NotNull(message = "checkIn must not be null")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @Schema(
                description = "Check-out date",
                example = "31/12/2026"
        )
        @NotNull(message = "checkOut must not be null")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        @Schema(
                description = "Guest ages. Order is significant.",
                example = "[30, 29, 1, 3]"
        )
        @NotNull(message = "ages must not be null")
        @NotEmpty(message = "ages must not be empty")
        List<@NotNull @PositiveOrZero Integer> ages

) {
    public SearchRequestDTO {
        ages = ages == null ? null : List.copyOf(ages);
    }
}