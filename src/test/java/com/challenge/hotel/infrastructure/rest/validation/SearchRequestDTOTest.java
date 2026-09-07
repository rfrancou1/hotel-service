package com.challenge.hotel.infrastructure.rest.validation;

import com.challenge.hotel.infrastructure.rest.dto.SearchRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchRequestDTOTest {

    @Test
    void shouldRejectCheckInInThePast() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            SearchRequestDTO request = new SearchRequestDTO(
                    "1234aBc",
                    LocalDate.now().minusDays(1),
                    LocalDate.now().plusDays(1),
                    List.of(30, 29)
            );

            Set<ConstraintViolation<SearchRequestDTO>> violations =
                    validator.validate(request);

            assertTrue(
                    violations.stream()
                            .anyMatch(violation ->
                                    violation.getPropertyPath().toString().equals("checkIn")
                                            && violation.getMessage().equals(
                                            "checkIn must be today or in the future"
                                    )
                            )
            );
        }
    }
}
