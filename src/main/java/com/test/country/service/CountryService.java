package com.test.country.service;

import com.test.country.api.*;
import com.test.country.client.CountryClient;
import com.test.country.client.api.CountryDTO;
import com.test.country.convert.CountryConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
public class CountryService {

    @Value("${country.client.query.all.path}")
    private String QUERY_ALL_PATH;

    @Value("${country.client.query.name.path}")
    private String QUERY_BY_NAME_PATH;

    @Autowired
    private CountryClient countryClient;

    public Flux<CountryListEvent> queryCountriesAsync() {
        return callActualServiceToQueryAllAsync();
    }

    private Flux<CountryListEvent> callActualServiceToQueryAllAsync() {
        ErrorMessage errorMessage = new ErrorMessage();
        return constructQueryAll()
                .onStatus(HttpStatus::is4xxClientError, error -> buildResponseException(error, errorMessage))
                .onStatus(HttpStatus::is5xxServerError, error -> buildResponseException(error, errorMessage))
                .bodyToFlux(CountryDTO.class)
                .onErrorResume(e -> {
                    if(Objects.isNull(errorMessage.getStatus())){//Error might happen before actual call
                        errorMessage.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        errorMessage.setMessage(e.getMessage());
                    }
                    return Mono.just(new CountryDTO());
                })
                .map(c -> CountryConverter.convertToCountryList(c, errorMessage));
    }

    public Flux<CountryEvent> queryCountryByNameAsync(String name) {
        return callActualServiceToQueryCountryAsync(name);
    }

    private Flux<CountryEvent> callActualServiceToQueryCountryAsync(String name) {
        ErrorMessage errorMessage = new ErrorMessage();
        return constructQueryCountry(name)
                .onStatus(HttpStatus::is4xxClientError, error -> buildResponseException(error, errorMessage))
                .onStatus(HttpStatus::is5xxServerError, error -> buildResponseException(error, errorMessage))
                .bodyToFlux(CountryDTO.class)
                .onErrorResume(e -> {
                    if(Objects.isNull(errorMessage.getStatus())){
                        errorMessage.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        errorMessage.setMessage(e.getMessage());
                    }
                    return Mono.just(new CountryDTO());
                })
                .map(c -> CountryConverter.convertToCountry(c, errorMessage));
    }


    public List<CountryList> queryCountriesSync() {
        return callActualServiceToQueryAllSync().collectList().block();
    }

    private Flux<CountryList> callActualServiceToQueryAllSync() {
        return constructQueryAll()
                .onStatus(HttpStatus::is4xxClientError, this::buildException)
                .onStatus(HttpStatus::is5xxServerError, this::buildException)
                .bodyToFlux(CountryDTO.class)
                .map(CountryConverter::convertToCountryList);
    }

    public Country queryCountryByNameSync(String name) {
        return callActualServiceToQueryCountrySync(name).blockLast();
    }

    private Flux<Country> callActualServiceToQueryCountrySync(String name) {
        return constructQueryCountry(name)
                .onStatus(HttpStatus::is4xxClientError, this::buildException)
                .onStatus(HttpStatus::is5xxServerError, this::buildException)
                .bodyToFlux(CountryDTO.class)
                .map(CountryConverter::convertToCountry);
    }

    private WebClient.ResponseSpec constructQueryCountry(String name) {
        return countryClient.getClient().get().uri(uriBuilder -> uriBuilder.path(countryClient.getCountryClientUrl() + QUERY_BY_NAME_PATH).build(name))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).retrieve();
    }

    private WebClient.ResponseSpec constructQueryAll() {
        return countryClient.getClient().get().uri(countryClient.getCountryClientUrl() + QUERY_ALL_PATH)
                .retrieve();
    }

    private Mono<ResponseStatusException> buildResponseException(ClientResponse error, ErrorMessage errorMessage) {
        return error.bodyToMono(ErrorMessage.class).flatMap(e -> {
            errorMessage.setStatus(error.statusCode().value());
            errorMessage.setMessage(e.getMessage());
            return Mono.error(new ResponseStatusException(error.statusCode(), e.getMessage()));
        });
    }

    private Mono<ResponseStatusException> buildException(ClientResponse error) {
        return error.bodyToMono(ErrorMessage.class).flatMap(e -> Mono.error(new ResponseStatusException(error.statusCode(), e.getMessage())));
    }

}
