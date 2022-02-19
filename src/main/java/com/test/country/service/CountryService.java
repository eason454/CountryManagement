package com.test.country.service;

import com.test.country.api.Country;
import com.test.country.client.CountryClient;
import com.test.country.client.api.CountryDTO;
import com.test.country.convert.CountryConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CountryService {

    @Value("${country.client.query.all.path}")
    private String QUERY_ALL_BY_PATH;

    @Value("${country.client.query.name.path}")
    private String QUERY_BY_NAME_PATH;

    @Autowired
    private CountryClient countryClient;

    public Flux<Country> queryCountries() {
        return countryClient.getClient().get().uri(countryClient.getCountryClientUrl() + QUERY_ALL_BY_PATH)
                .retrieve().bodyToFlux(CountryDTO.class).map(CountryConverter::convertToCountry);
    }

    public Mono<Country> queryCountryByName(String name) {
        return countryClient.getClient().get().uri(uriBuilder -> uriBuilder.path(countryClient.getCountryClientUrl() + QUERY_BY_NAME_PATH).build(name))
                .retrieve().bodyToMono(CountryDTO.class).map(CountryConverter::convertToCountry);

    }

}
