package com.challenge.hotel.infrastructure.config;

import com.challenge.hotel.application.port.in.CountSearchUseCase;
import com.challenge.hotel.application.port.in.CreateSearchUseCase;
import com.challenge.hotel.application.port.in.SaveSearchUseCase;
import com.challenge.hotel.application.port.out.SearchEventPublisher;
import com.challenge.hotel.domain.repository.SearchRepository;
import com.challenge.hotel.application.service.CountSearchService;
import com.challenge.hotel.application.service.CreateSearchService;
import com.challenge.hotel.application.service.SaveSearchService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    CreateSearchUseCase createSearchUseCase(
            SearchEventPublisher searchEventPublisher) {

        return new CreateSearchService(searchEventPublisher);
    }

    @Bean
    SaveSearchUseCase saveSearchUseCase(
            SearchRepository searchRepository) {

        return new SaveSearchService(searchRepository);
    }

    @Bean
    CountSearchUseCase countSearchUseCase(
            SearchRepository searchRepository) {

        return new CountSearchService(searchRepository);
    }
}