package com.challenge.hotel.infrastructure.persistence;

import com.challenge.hotel.domain.model.HotelSearch;
import com.challenge.hotel.infrastructure.persistence.adapter.PostgresSearchRepository;
import com.challenge.hotel.infrastructure.persistence.repository.SpringDataSearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresSearchRepository.class)
class PostgresSearchRepositoryTest {

    @Container
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("hotel")
                    .withUsername("hotel")
                    .withPassword("hotel");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PostgresSearchRepository repository;

    @Autowired
    private SpringDataSearchRepository springDataRepository;

    @Test
    void shouldSaveAndFindSearchById() {
        UUID searchId = UUID.randomUUID();

        HotelSearch search = new HotelSearch(
                searchId,
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );

        repository.save(search);

        Optional<HotelSearch> result = repository.findById(searchId);

        assertTrue(result.isPresent());

        HotelSearch savedSearch = result.get();

        assertAll(
                () -> assertEquals(searchId, savedSearch.searchId()),
                () -> assertEquals("1234aBc", savedSearch.hotelId()),
                () -> assertEquals(
                        LocalDate.of(2026, 12, 29),
                        savedSearch.checkIn()
                ),
                () -> assertEquals(
                        LocalDate.of(2026, 12, 31),
                        savedSearch.checkOut()
                ),
                () -> assertEquals(
                        List.of(30, 29, 1, 3),
                        savedSearch.ages()
                )
        );
    }

    @Test
    void shouldReturnEmptyWhenSearchDoesNotExist() {
        Optional<HotelSearch> result =
                repository.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCountMatchingSearches() {
        HotelSearch firstSearch = createSearch(
                UUID.randomUUID(),
                List.of(30, 29, 1, 3)
        );

        HotelSearch secondSearch = createSearch(
                UUID.randomUUID(),
                List.of(30, 29, 1, 3)
        );

        repository.save(firstSearch);
        repository.save(secondSearch);

        long count = repository.countMatching(firstSearch);

        assertEquals(2L, count);
    }

    @Test
    void shouldConsiderAgesOrderWhenCounting() {
        HotelSearch originalOrder = createSearch(
                UUID.randomUUID(),
                List.of(30, 29, 1, 3)
        );

        HotelSearch differentOrder = createSearch(
                UUID.randomUUID(),
                List.of(3, 29, 30, 1)
        );

        repository.save(originalOrder);
        repository.save(differentOrder);

        long originalCount =
                repository.countMatching(originalOrder);

        long differentOrderCount =
                repository.countMatching(differentOrder);

        assertAll(
                () -> assertEquals(1L, originalCount),
                () -> assertEquals(1L, differentOrderCount)
        );
    }

    @Test
    void shouldNotCountSearchWithDifferentHotel() {
        HotelSearch original = createSearch(
                UUID.randomUUID(),
                List.of(30, 29, 1, 3)
        );

        HotelSearch differentHotel = new HotelSearch(
                UUID.randomUUID(),
                "anotherHotel",
                original.checkIn(),
                original.checkOut(),
                original.ages()
        );

        repository.save(original);
        repository.save(differentHotel);

        long count = repository.countMatching(original);

        assertEquals(1L, count);
    }

    private HotelSearch createSearch(
            UUID searchId,
            List<Integer> ages
    ) {
        return new HotelSearch(
                searchId,
                "1234aBc",
                LocalDate.of(2026, 12, 29),
                LocalDate.of(2026, 12, 31),
                ages
        );
    }
}