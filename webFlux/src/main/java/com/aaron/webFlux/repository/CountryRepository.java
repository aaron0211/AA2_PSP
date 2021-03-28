package com.aaron.webFlux.repository;

import com.aaron.webFlux.domain.Country;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CountryRepository extends ReactiveMongoRepository<Country, Long> {

    Flux<Country> findAll();
    Mono<Country> findByName(String name);
    Mono<Country> findById(String id);
}
