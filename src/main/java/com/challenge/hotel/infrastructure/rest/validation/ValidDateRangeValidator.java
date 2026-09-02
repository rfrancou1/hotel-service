package com.challenge.hotel.infrastructure.rest.validation;

import com.challenge.hotel.infrastructure.rest.dto.SearchRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRangeValidator
        implements ConstraintValidator<ValidDateRange, SearchRequestDTO> {

    @Override
    public boolean isValid(
            SearchRequestDTO request,
            ConstraintValidatorContext context
    ) {
        if (request == null) {
            return true;
        }

        if (request.checkIn() == null || request.checkOut() == null) {
            return true;
        }

        return request.checkIn().isBefore(request.checkOut());
    }
}