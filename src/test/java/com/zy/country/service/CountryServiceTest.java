package com.zy.country.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zy.country.data.Country;
import com.zy.country.data.CountryEvent;
import com.zy.country.data.CountryList;
import com.zy.country.data.CountryListEvent;
import com.zy.country.data.ErrorMessage;
import com.zy.country.data.EventType;
import com.zy.country.client.api.CountryDTO;
import com.zy.country.client.api.Flag;
import com.zy.country.client.api.Name;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountryServiceTest {
    public static MockWebServer mockServer;
    private CountryService countryService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String queryNamePath = "/name/{name}";
    private final String queryAllPath = "/all";
    private final String countryName = "Finland";

    @BeforeAll
    static void setup() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void teardown() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("localhost:%s",
                mockServer.getPort());
        countryService = new CountryService();
        ReflectionTestUtils.setField(countryService, "countryClientUrl", baseUrl);
        ReflectionTestUtils.setField(countryService, "queryNamePath", queryNamePath);
        ReflectionTestUtils.setField(countryService, "queryAllPath", queryAllPath);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenCountryName_whenQueryByName_thenReturnCountry(boolean isAsync) throws Exception {
        CountryDTO countryDTO = mockCountry();
        mockServer.enqueue(new MockResponse().setBody(mapper.writeValueAsString(countryDTO))
                .addHeader("Content-Type", "application/json"));
        if (isAsync) {
            Flux<CountryEvent> country = countryService.queryCountryByNameAsync(countryName);
            StepVerifier.create(country).expectNextMatches(e -> isAllCountryEventFieldsSame(countryDTO, e))
                    .verifyComplete();
        } else {
            Country country = countryService.queryCountryByNameSync(countryName);
            assertTrue(isAllCountryFieldsSame(countryDTO, country));
        }

        checkRequest("/name/" + countryName);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void whenQueryAllCountries_thenReturnCountries(boolean isAsync) throws Exception {
        CountryDTO countryDTO = mockCountry();
        mockServer.enqueue(new MockResponse().setBody(mapper.writeValueAsString(asList(countryDTO)))
                .addHeader("Content-Type", "application/json"));
        if (isAsync) {
            Flux<CountryListEvent> country = countryService.queryCountriesAsync();
            StepVerifier.create(country).expectNextMatches(e -> isAllCountryListEventFieldsSame(countryDTO, e))
                    .verifyComplete();
        } else {
            List<CountryList> countryList = countryService.queryCountriesSync();
            assertEquals(1, countryList.size());
            assertTrue(isAllCountryListFieldsSame(countryDTO, countryList.get(0)));
        }
        checkRequest(queryAllPath);
    }


    @Test
    void givenCountryName_whenQueryByName_thenReturnException() throws Exception {
        //Server 4xx and 5xx error
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setStatus(HttpStatus.NOT_FOUND.value());
        errorMessage.setMessage("Not Found");
        mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()).setBody(mapper.writeValueAsString(errorMessage))
                .addHeader("Content-Type", "application/json"));
        Flux<CountryEvent> country = countryService.queryCountryByNameAsync(countryName);
        StepVerifier.create(country).expectNextMatches(e -> isMatchErrorMessage(e, errorMessage.getStatus(), errorMessage.getMessage()))
                .verifyComplete();

        errorMessage.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorMessage.setMessage("exception");
        mockServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).setBody(mapper.writeValueAsString(errorMessage))
                .addHeader("Content-Type", "application/json"));
        country = countryService.queryCountryByNameAsync(countryName);
        StepVerifier.create(country).expectNextMatches(e -> isMatchErrorMessage(e, errorMessage.getStatus(), errorMessage.getMessage()))
                .verifyComplete();

        //UnknownHost exception before server responds
        CountryDTO countryDTO = mockCountry();
        mockServer.enqueue(new MockResponse().setBody(mapper.writeValueAsString(countryDTO))
                .addHeader("Content-Type", "application/json"));
        countryService = new CountryService();
        ReflectionTestUtils.setField(countryService, "countryClientUrl", "unKnownHost");
        country = countryService.queryCountryByNameAsync(countryName);
        StepVerifier.create(country).expectNextMatches(e -> isMatchErrorMessage(e, HttpStatus.INTERNAL_SERVER_ERROR.value(), "unKnownHost"))
                .verifyComplete();
        checkRequest("/name/" + countryName);
    }

    private boolean isMatchErrorMessage(CountryEvent e, int statusCode, String message) {
        return e.getType() == EventType.ERROR && Objects.isNull(e.getCountry()) && Objects.equals(e.getError().getStatus(), statusCode) && e.getError().getMessage().contains(message);
    }


    private void checkRequest(String path) throws InterruptedException {
        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertEquals(HttpMethod.GET.name(), recordedRequest.getMethod());
        assertEquals(recordedRequest.getPath(), path);
    }

    private boolean isAllCountryListEventFieldsSame(CountryDTO countryDTO, CountryListEvent e) {
        return Objects.equals(e.getCountry().getName(), countryDTO.getName().getCommon()) &&
                Objects.equals(e.getCountry().getCountryCode(), countryDTO.getCountryCode());
    }

    private boolean isAllCountryListFieldsSame(CountryDTO countryDTO, CountryList e) {
        return Objects.equals(e.getName(), countryDTO.getName().getCommon()) &&
                Objects.equals(e.getCountryCode(), countryDTO.getCountryCode());
    }


    private boolean isAllCountryEventFieldsSame(CountryDTO countryDTO, CountryEvent e) {
        return Objects.equals(e.getCountry().getName(), countryDTO.getName().getCommon()) &&
                Objects.equals(e.getCountry().getCountryCode(), countryDTO.getCountryCode()) &&
                Objects.equals(e.getCountry().getPopulation(), countryDTO.getPopulation()) &&
                Objects.equals(e.getCountry().getCapital(), countryDTO.getCapital().get(0)) &&
                Objects.equals(e.getCountry().getFlagFileUrl(), countryDTO.getFlag().getPng());
    }

    private boolean isAllCountryFieldsSame(CountryDTO countryDTO, Country e) {
        return Objects.equals(e.getName(), countryDTO.getName().getCommon()) &&
                Objects.equals(e.getCountryCode(), countryDTO.getCountryCode()) &&
                Objects.equals(e.getPopulation(), countryDTO.getPopulation()) &&
                Objects.equals(e.getCapital(), countryDTO.getCapital().get(0)) &&
                Objects.equals(e.getFlagFileUrl(), countryDTO.getFlag().getPng());
    }


    private CountryDTO mockCountry() {
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setCountryCode("FI");
        Name nameDto = new Name();
        nameDto.setCommon(countryName);
        countryDTO.setName(nameDto);
        countryDTO.setCapital(asList("Helsinki"));
        countryDTO.setPopulation(5491817L);
        Flag flag = new Flag();
        flag.setPng("http://1234.png");
        countryDTO.setFlag(flag);
        return countryDTO;
    }
}
