package com.aaron.webFlux.controller;

import com.aaron.webFlux.domain.Country;
import com.aaron.webFlux.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;


@RestController
public class CountryController {

    private final Logger logger = LoggerFactory.getLogger(CountryController.class);

    @Autowired
    private CountryService countryService;

    @GetMapping(value = "/countries", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Country> getCountries(){
        return countryService.findAllCountries();
    }

    @GetMapping("/countries/{id}")
    public Mono<Country> getCountry(@PathParam("id") String id){
        return countryService.findCountry(id);
    }
}
