package com.challenge.hotel.infrastructure.rest;

import com.challenge.hotel.application.model.SearchCommand;
import com.challenge.hotel.application.model.SearchCount;
import com.challenge.hotel.application.port.in.CountSearchUseCase;
import com.challenge.hotel.application.port.in.CreateSearchUseCase;
import com.challenge.hotel.domain.model.HotelSearch;
import com.challenge.hotel.infrastructure.rest.dto.SearchCountResponseDTO;
import com.challenge.hotel.infrastructure.rest.dto.SearchDetailsDTO;
import com.challenge.hotel.infrastructure.rest.dto.SearchRequestDTO;
import com.challenge.hotel.infrastructure.rest.dto.SearchResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(
        name = "Hotel Search",
        description = "Operations for hotel availability searches"
)
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
    @Operation(
            summary = "Create a hotel search",
            description = "Creates a search identifier and publishes the search asynchronously to Kafka."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Search accepted"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    public ResponseEntity<SearchResponseDTO> createSearch(
            @Valid @RequestBody SearchRequestDTO request
    ) {
        SearchCommand command = new SearchCommand(
                request.hotelId(),
                request.checkIn(),
                request.checkOut(),
                request.ages()
        );

        UUID searchId = createSearchUseCase.create(command);

        return ResponseEntity
                .accepted()
                .body(new SearchResponseDTO(searchId));
    }

    @GetMapping("/count")
    @Operation(
            summary = "Count equivalent searches",
            description = "Returns how many persisted searches match the search identified by searchId."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Count successfully returned"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Search identifier not found"
            )
    })
    public ResponseEntity<SearchCountResponseDTO> count(
            @Parameter(
                    description = "Identifier returned by POST /search",
                    required = true
            )
            @RequestParam UUID searchId
    ) {
        SearchCount result = countSearchUseCase.getCount(searchId);

        HotelSearch search = result.search();

        SearchDetailsDTO details =
                new SearchDetailsDTO(
                        search.hotelId(),
                        search.checkIn(),
                        search.checkOut(),
                        search.ages()
                );

        return ResponseEntity.ok(
                new SearchCountResponseDTO(
                        search.searchId(),
                        details,
                        result.count()
                )
        );
    }
}