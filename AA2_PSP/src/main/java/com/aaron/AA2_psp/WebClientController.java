package com.aaron.AA2_psp;

import com.aaron.AA2_psp.domain.Country;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

public class WebClientController {

    public WebClientController() {
        WebClient webClient = WebClient.create("http://localhost:8080");
        Flux<Country> countryFlux = webClient.get()
                .uri("/countries")
                .retrieve()
                .bodyToFlux(Country.class);

        countryFlux.doOnError((System.out::println))
                .subscribeOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
                .doOnComplete(()-> System.out.println("Terminado"))
                .subscribe((country)-> System.out.println(country.getName()));
    }
}
