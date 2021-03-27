package com.aaron.AA2_psp.service;

import com.aaron.AA2_psp.domain.Country;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

import java.util.List;

public interface CountrieApiService {

    @GET("all")
    Observable<List<Country>> getAll();

    @GET("lang/es")
    Observable<List<Country>> getSpanish();
}
