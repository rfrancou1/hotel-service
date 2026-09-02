package com.challenge.hotel.infrastructure.persistence.adapter;

import com.challenge.hotel.application.port.out.SearchRepository;
import com.challenge.hotel.domain.model.HotelSearch;
import com.challenge.hotel.infrastructure.persistence.entity.SearchEntity;
import com.challenge.hotel.infrastructure.persistence.repository.SpringDataSearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresSearchRepository implements SearchRepository {

    private final SpringDataSearchRepository repository;

    public PostgresSearchRepository(
            SpringDataSearchRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void save(HotelSearch search) {

        SearchEntity entity = new SearchEntity(
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages().toArray(Integer[]::new),
                LocalDateTime.now()
        );

        repository.save(entity);
    }

    @Override
    public Optional<HotelSearch> findById(UUID searchId) {
        return repository.findById(searchId)
                .map(this::toDomain);
    }

    @Override
    public long countMatching(HotelSearch search) {
        return repository.countMatching(
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages().toArray(Integer[]::new)
        );
    }

    private HotelSearch toDomain(SearchEntity entity) {
        return new HotelSearch(
                entity.getId(),
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                Arrays.asList(entity.getAges())
        );
    }
}