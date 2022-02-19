package com.test.country.controller;

import com.test.country.api.Country;
import com.test.country.service.CountryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/countries", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public class CountryController {

    @Autowired
    private CountryService countryService;

    public Flux<Country> queryCountries() {
        return countryService.queryCountries();
    }

    @GetMapping("/name/{name}")
    public Mono<Country> queryCountriesByName(@PathVariable(value = "name") String name) {
        return countryService.queryCountryByName(name);
    }
}
