package com.challenge.hotel.infrastructure.persistence.repository;

import com.challenge.hotel.infrastructure.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface SpringDataSearchRepository
        extends JpaRepository<SearchEntity, UUID> {

    @Query(
            value = """
                    SELECT COUNT(*)
                    FROM hotel_search
                    WHERE hotel_id = :hotelId
                      AND check_in = :checkIn
                      AND check_out = :checkOut
                      AND ages = CAST(:ages AS INTEGER[])
                    """,
            nativeQuery = true
    )
    long countMatching(
            @Param("hotelId") String hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("ages") Integer[] ages
    );
}