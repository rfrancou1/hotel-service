package com.challenge.hotel.infrastructure.rest;

import com.challenge.hotel.application.model.SearchCommand;
import com.challenge.hotel.application.model.SearchCount;
import com.challenge.hotel.application.port.in.CountSearchUseCase;
import com.challenge.hotel.application.port.in.CreateSearchUseCase;
import com.challenge.hotel.infrastructure.rest.dto.SearchCountResponseDTO;
import com.challenge.hotel.infrastructure.rest.dto.SearchDetailsDTO;
import com.challenge.hotel.infrastructure.rest.dto.SearchRequestDTO;
import com.challenge.hotel.infrastructure.rest.dto.SearchResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class SearchController {

    private final CreateSearchUseCase createSearchUseCase;
    private final CountSearchUseCase countSearchUseCase;

    public SearchController(
            CreateSearchUseCase createSearchUseCase,
            CountSearchUseCase countSearchUseCase
    ) {
        this.createSearchUseCase = createSearchUseCase;
        this.countSearchUseCase = countSearchUseCase;
    }

    @PostMapping("/search")
    public ResponseEntity<SearchResponseDTO> search(
            @Valid @RequestBody SearchRequestDTO request
    ) {

        SearchCommand command = new SearchCommand(
                request.hotelId(),
                request.checkIn(),
                request.checkOut(),
                request.ages()
        );

        UUID searchId = createSearchUseCase.create(command);

        return ResponseEntity.accepted()
                .body(new SearchResponseDTO(searchId));
    }

    @GetMapping("/count")
    public ResponseEntity<SearchCountResponseDTO> count(
            @RequestParam UUID searchId
    ) {

        SearchCount result = countSearchUseCase.getCount(searchId);

        SearchDetailsDTO search = new SearchDetailsDTO(
                result.search().hotelId(),
                result.search().checkIn(),
                result.search().checkOut(),
                result.search().ages()
        );

        SearchCountResponseDTO response = new SearchCountResponseDTO(
                result.search().searchId(),
                search,
                result.count()
        );

        return ResponseEntity.ok(response);
    }
}