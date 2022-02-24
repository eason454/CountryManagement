package com.zy.country.service;

import com.zy.country.client.api.CountryDTO;
import com.zy.country.convert.CountryConverter;
import com.zy.country.data.Country;
import com.zy.country.data.CountryEvent;
import com.zy.country.data.CountryList;
import com.zy.country.data.CountryListEvent;
import com.zy.country.data.ErrorMessage;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * Service that connects to REST APIs from 3rd server. Providing both standard and reactive call.
 */

@Service
public class CountryService {

    @Value("${country.client.query.all.path}")
    private String queryAllPath;

    @Value("${country.client.query.name.path}")
    private String queryNamePath;

    @Value("${country.client.url}")
    private String countryClientUrl;

    private WebClient webClient;

    public CountryService() {
        this.webClient = buildWebClient();
    }

    /**
     * This is asynchronous call, if error happens during call, method still returns an event containing error message without country
     * information.
     */
    public Flux<CountryListEvent> queryCountriesAsync() {
        ErrorMessage errorMessage = new ErrorMessage();
        return constructQueryAll()
                .onStatus(HttpStatus::is4xxClientError, error -> buildResponseException(error, errorMessage))
                .onStatus(HttpStatus::is5xxServerError, error -> buildResponseException(error, errorMessage))
                .bodyToFlux(CountryDTO.class)
                .onErrorResume(e -> getCountryDTOMono(errorMessage, e))
                .map(c -> CountryConverter.convertToCountryList(c, errorMessage));
    }

    public Flux<CountryEvent> queryCountryByNameAsync(String name) {
        ErrorMessage errorMessage = new ErrorMessage();
        return constructQueryCountry(name)
                //Handle server error
                .onStatus(HttpStatus::is4xxClientError, error -> buildResponseException(error, errorMessage))
                .onStatus(HttpStatus::is5xxServerError, error -> buildResponseException(error, errorMessage))
                .bodyToFlux(CountryDTO.class)
                //Resume from error and return the event containing error
                .onErrorResume(e -> getCountryDTOMono(errorMessage, e))
                .map(c -> CountryConverter.convertToCountry(c, errorMessage));
    }

    public List<CountryList> queryCountriesSync() {
        return constructQueryAll()
                .onStatus(HttpStatus::is4xxClientError, this::buildResponseException)
                .onStatus(HttpStatus::is5xxServerError, this::buildResponseException)
                .bodyToFlux(CountryDTO.class)
                .map(CountryConverter::convertToCountryList).collectList().block();
    }

    public Country queryCountryByNameSync(String name) {
        return constructQueryCountry(name)
                .onStatus(HttpStatus::is4xxClientError, this::buildResponseException)
                .onStatus(HttpStatus::is5xxServerError, this::buildResponseException)
                .bodyToFlux(CountryDTO.class)
                .map(CountryConverter::convertToCountry).blockLast();
    }


    private WebClient.ResponseSpec constructQueryCountry(String name) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(countryClientUrl + queryNamePath).build(name))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).retrieve();
    }

    private WebClient.ResponseSpec constructQueryAll() {
        return webClient.get().uri(countryClientUrl + queryAllPath)
                .retrieve();
    }

    private Mono<ResponseStatusException> buildResponseException(ClientResponse error, ErrorMessage errorMessage) {
        return error.bodyToMono(ErrorMessage.class).flatMap(e -> {
            errorMessage.setStatus(error.statusCode().value());
            errorMessage.setMessage(e.getMessage());
            return Mono.error(new ResponseStatusException(error.statusCode(), e.getMessage()));
        });
    }

    private Mono<ResponseStatusException> buildResponseException(ClientResponse error) {
        return error.bodyToMono(ErrorMessage.class).flatMap(e -> Mono.error(new ResponseStatusException(error.statusCode(), e.getMessage())));
    }


    /**
     * If error happens before actual server call, convert exception to the event containing error information
     */
    private Publisher<? extends CountryDTO> getCountryDTOMono(ErrorMessage errorMessage, Throwable e) {
        if (Objects.isNull(errorMessage.getStatus())) {
            errorMessage.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorMessage.setMessage(e.getMessage());
        }
        return Mono.just(new CountryDTO());
    }


    private WebClient buildWebClient() {
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(
                //Handle 301 Moved Permanently
                HttpClient.create().followRedirect(true)
        )).build();
    }

}
