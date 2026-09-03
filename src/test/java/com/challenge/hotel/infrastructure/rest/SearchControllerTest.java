package com.challenge.hotel.infrastructure.rest;

import com.challenge.hotel.application.model.SearchCommand;
import com.challenge.hotel.application.model.SearchCount;
import com.challenge.hotel.application.port.in.CountSearchUseCase;
import com.challenge.hotel.application.port.in.CreateSearchUseCase;
import com.challenge.hotel.domain.exception.SearchNotFoundException;
import com.challenge.hotel.domain.model.HotelSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateSearchUseCase createSearchUseCase;

    @MockitoBean
    private CountSearchUseCase countSearchUseCase;

    @Test
    void shouldCreateSearch() throws Exception {
        UUID searchId = UUID.randomUUID();

        when(createSearchUseCase.create(any(SearchCommand.class)))
                .thenReturn(searchId);

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": "1234aBc",
                                  "checkIn": "29/12/2026",
                                  "checkOut": "31/12/2026",
                                  "ages": [30, 29, 1, 3]
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchId").value(searchId.toString()));

        verify(createSearchUseCase).create(any(SearchCommand.class));
        verifyNoInteractions(countSearchUseCase);
    }

    @Test
    void shouldReturnSearchCount() throws Exception {
        UUID searchId = UUID.randomUUID();

        HotelSearch search = new HotelSearch(
                searchId,
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        when(countSearchUseCase.getCount(searchId))
                .thenReturn(new SearchCount(search, 3L));

        mockMvc.perform(get("/count")
                        .param("searchId", searchId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.searchId").value(searchId.toString()))
                .andExpect(jsonPath("$.search.hotelId").value("1234aBc"))
                .andExpect(jsonPath("$.search.checkIn").value("29/12/2026"))
                .andExpect(jsonPath("$.search.checkOut").value("31/12/2026"))
                .andExpect(jsonPath("$.search.ages[0]").value(30))
                .andExpect(jsonPath("$.search.ages[1]").value(29))
                .andExpect(jsonPath("$.search.ages[2]").value(1))
                .andExpect(jsonPath("$.search.ages[3]").value(3))
                .andExpect(jsonPath("$.count").value(3));

        verify(countSearchUseCase).getCount(searchId);
        verifyNoInteractions(createSearchUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": "",
                                  "checkIn": "31/12/2026",
                                  "checkOut": "29/12/2026",
                                  "ages": []
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createSearchUseCase);
        verifyNoInteractions(countSearchUseCase);
    }

    @Test
    void shouldReturnNotFoundWhenSearchDoesNotExist() throws Exception {
        UUID searchId = UUID.randomUUID();

        when(countSearchUseCase.getCount(searchId))
                .thenThrow(new SearchNotFoundException(searchId));

        mockMvc.perform(get("/count")
                        .param("searchId", searchId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Search not found: %s".formatted(searchId)));

        verify(countSearchUseCase).getCount(searchId);
        verifyNoInteractions(createSearchUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenCheckInIsAfterCheckOut() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "hotelId": "1234aBc",
                              "checkIn": "31/12/2026",
                              "checkOut": "29/12/2026",
                              "ages": [30, 29, 1, 3]
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createSearchUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenAgesIsEmpty() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "hotelId": "1234aBc",
                              "checkIn": "29/12/2026",
                              "checkOut": "31/12/2026",
                              "ages": []
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createSearchUseCase);
    }
}