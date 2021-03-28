package com.aaron.webFlux.service;

import com.aaron.webFlux.domain.Country;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CountryService {

    Flux<Country> findAllCountries();
    Mono<Country> findCountryByName(String name);
    Mono<Country> findCountry(String id);
}
