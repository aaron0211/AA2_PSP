package com.aaron.AA2_psp.service;

import com.aaron.AA2_psp.domain.Country;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

import java.util.List;

import static com.aaron.AA2_psp.util.Constans.URL;

public class CountrieService {

    private CountrieApiService api;

    public CountrieService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        api = retrofit.create(CountrieApiService.class);
    }

    public Observable<List<Country>> getAll(){
        return api.getAll();
    }

    public Observable<List<Country>> getSpanish(){
        return api.getSpanish();
    }
}
