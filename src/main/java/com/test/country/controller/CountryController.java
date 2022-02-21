package com.test.country.controller;

import com.test.country.api.Country;
import com.test.country.api.CountryEvent;
import com.test.country.api.CountryList;
import com.test.country.api.CountryListEvent;
import com.test.country.service.CountryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping(path = "/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CountryListEvent> queryCountriesAsync() {
        return countryService.queryCountriesAsync();
    }

    @GetMapping(value= "/name/{name}",  produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CountryEvent> queryCountryByNameAsync(@PathVariable(value = "name") String name) {
        return countryService.queryCountryByNameAsync(name);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CountryList> queryCountriesSync() {
        return countryService.queryCountriesSync();
    }

    @GetMapping(value= "/name/{name}",  produces = MediaType.APPLICATION_JSON_VALUE)
    public Country queryCountryByNameSync(@PathVariable(value = "name") String name) {
        return countryService.queryCountryByNameSync(name);
    }
}
