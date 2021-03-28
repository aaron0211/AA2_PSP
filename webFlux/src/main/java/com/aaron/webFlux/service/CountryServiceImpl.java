package com.aaron.webFlux.service;

import com.aaron.webFlux.domain.Country;
import com.aaron.webFlux.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CountryServiceImpl implements CountryService{

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Flux<Country> findAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Mono<Country> findCountryByName(String name) {
        return countryRepository.findByName(name);
    }

    @Override
    public Mono<Country> findCountry(String id) {
        return countryRepository.findById(id);
    }
}
