package com.challenge.hotel.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hotel_search")
public class SearchEntity {

    @Id
    private UUID id;

    @Column(name = "hotel_id", nullable = false)
    private String hotelId;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "ages", columnDefinition = "integer[]", nullable = false)
    private Integer[] ages;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected SearchEntity() {
    }

    public SearchEntity(
            UUID id,
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer[] ages,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = ages.clone();
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public Integer[] getAges() {
        return ages.clone();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}