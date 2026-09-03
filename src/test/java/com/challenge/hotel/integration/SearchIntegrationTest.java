package com.challenge.hotel.integration;

import com.challenge.hotel.infrastructure.persistence.repository.SpringDataSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.sql.init.mode=never"
        }
)
class SearchIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("hotel")
                    .withUsername("hotel")
                    .withPassword("hotel");

    @Container
    @ServiceConnection
    static final KafkaContainer kafka =
            new KafkaContainer("apache/kafka-native:4.1.1");

    @LocalServerPort
    private int port;

    @Autowired
    private SpringDataSearchRepository repository;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldCreateSearchConsumeKafkaAndReturnCount() {
        CreateSearchResponse response = createSearch("""
                {
                  "hotelId": "1234aBc",
                  "checkIn": "29/12/2026",
                  "checkOut": "31/12/2026",
                  "ages": [30, 29, 1, 3]
                }
                """);

        assertNotNull(response);
        assertNotNull(response.searchId());

        waitUntilSearchIsPersisted(response.searchId());

        CountSearchResponse countResponse = getCount(response.searchId());

        assertAll(
                () -> assertNotNull(countResponse),
                () -> assertEquals(response.searchId(), countResponse.searchId()),
                () -> assertEquals(1L, countResponse.count()),
                () -> assertEquals("1234aBc", countResponse.search().hotelId()),
                () -> assertEquals(
                        "29/12/2026",
                        countResponse.search().checkIn()
                ),
                () -> assertEquals(
                        "31/12/2026",
                        countResponse.search().checkOut()
                ),
                () -> assertArrayEquals(
                        new Integer[]{30, 29, 1, 3},
                        countResponse.search().ages()
                )
        );
    }

    @Test
    void shouldCountTwoEqualSearches() {
        CreateSearchResponse first = createSearch("""
                {
                  "hotelId": "1234aBc",
                  "checkIn": "29/12/2026",
                  "checkOut": "31/12/2026",
                  "ages": [30, 29, 1, 3]
                }
                """);

        CreateSearchResponse second = createSearch("""
                {
                  "hotelId": "1234aBc",
                  "checkIn": "29/12/2026",
                  "checkOut": "31/12/2026",
                  "ages": [30, 29, 1, 3]
                }
                """);

        waitUntilSearchIsPersisted(first.searchId());
        waitUntilSearchIsPersisted(second.searchId());

        CountSearchResponse response = getCount(first.searchId());

        assertEquals(2L, response.count());
    }

    @Test
    void shouldConsiderAgesOrderWhenCounting() {
        CreateSearchResponse original = createSearch("""
                {
                  "hotelId": "1234aBc",
                  "checkIn": "29/12/2026",
                  "checkOut": "31/12/2026",
                  "ages": [30, 29, 1, 3]
                }
                """);

        CreateSearchResponse differentOrder = createSearch("""
                {
                  "hotelId": "1234aBc",
                  "checkIn": "29/12/2026",
                  "checkOut": "31/12/2026",
                  "ages": [3, 29, 30, 1]
                }
                """);

        waitUntilSearchIsPersisted(original.searchId());
        waitUntilSearchIsPersisted(differentOrder.searchId());

        CountSearchResponse originalResponse =
                getCount(original.searchId());

        CountSearchResponse differentOrderResponse =
                getCount(differentOrder.searchId());

        assertAll(
                () -> assertEquals(1L, originalResponse.count()),
                () -> assertEquals(1L, differentOrderResponse.count())
        );
    }

    private CreateSearchResponse createSearch(String body) {
        return restClient.post()
                .uri("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(
                        status -> status.value() != HttpStatus.ACCEPTED.value(),
                        (request, response) -> {
                            throw new IllegalStateException(
                                    "Unexpected status: " + response.getStatusCode()
                            );
                        }
                )
                .body(CreateSearchResponse.class);
    }

    private CountSearchResponse getCount(UUID searchId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/count")
                        .queryParam("searchId", searchId)
                        .build())
                .retrieve()
                .body(CountSearchResponse.class);
    }

    private void waitUntilSearchIsPersisted(UUID searchId) {
        long deadline =
                System.nanoTime() + Duration.ofSeconds(10).toNanos();

        while (System.nanoTime() < deadline) {
            if (repository.existsById(searchId)) {
                return;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(exception);
            }
        }

        fail("Search was not persisted in time: " + searchId);
    }

    private record CreateSearchResponse(
            UUID searchId
    ) {
    }

    private record CountSearchResponse(
            UUID searchId,
            SearchDetails search,
            long count
    ) {
    }

    private record SearchDetails(
            String hotelId,
            String checkIn,
            String checkOut,
            Integer[] ages
    ) {
    }
}