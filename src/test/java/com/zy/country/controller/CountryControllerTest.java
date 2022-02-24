package com.zy.country.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.zy.country.data.Country;
import com.zy.country.data.CountryEvent;
import com.zy.country.data.CountryList;
import com.zy.country.data.CountryListEvent;
import com.zy.country.data.EventType;
import com.zy.country.service.CountryService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Flux;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CountryController.class)
class CountryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService service;

    private static Country country;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        country = mockCountryData();
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Test
    void givenCountryName_whenQueryByName_thenReturnCountry() throws Exception {
        when(service.queryCountryByNameSync(country.getName())).thenReturn(country);
        this.mockMvc.perform(get("/countries/name/" + country.getName()).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(country.getName()))
                .andExpect(jsonPath("$.country_code").value(country.getCountryCode()))
                .andExpect(jsonPath("$.population").value(country.getPopulation()))
                .andExpect(jsonPath("$.flag_file_url").value(country.getFlagFileUrl()))
                .andExpect(jsonPath("$.capital").value(country.getCapital()));

        CountryEvent countryEvent = mockCountryEventData();
        when(service.queryCountryByNameAsync(country.getName())).thenReturn(Flux.just(countryEvent));
        MvcResult result = this.mockMvc.perform(get("/countries/name/" + country.getName()).header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("data"))).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals(content.indexOf("{"), 5);//Response should be like: data: {}
        String jsonBody = content.substring(content.indexOf("{"));
        CountryEvent countryEventResponse = objectMapper.readValue(jsonBody, CountryEvent.class);
        assertEquals(countryEventResponse, countryEvent);
    }

    @Test
    void whenQueryAllCountries_thenReturnCountries() throws Exception {
        List<CountryList> countries = new ArrayList<>();
        countries.add(mockCountryListData());
        when(service.queryCountriesSync()).thenReturn(countries);
        this.mockMvc.perform(get("/countries/").header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name").value(country.getName()))
                .andExpect(jsonPath("[0].country_code").value(country.getCountryCode()));

        CountryListEvent countryListEvent = mockCountryListEventData();
        when(service.queryCountriesAsync()).thenReturn(Flux.just(countryListEvent));
        MvcResult result = this.mockMvc.perform(get("/countries/").header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("data"))).andReturn();
        String content = result.getResponse().getContentAsString();
        assertEquals(content.indexOf("{"), 5);//Response should be like: data: {}
        String jsonBody = content.substring(content.indexOf("{"));
        CountryListEvent countryListEventResponse = objectMapper.readValue(jsonBody, CountryListEvent.class);
        assertEquals(countryListEventResponse, countryListEvent);
    }


    private static Country mockCountryData() {
        Country country = new Country();
        country.setName("Finland");
        country.setCountryCode("FI");
        country.setPopulation(123L);
        country.setFlagFileUrl("http://test.png");
        country.setCapital("Helsinki");
        return country;
    }

    private static CountryList mockCountryListData() {
        CountryList countryList = new CountryList();
        countryList.setName("Finland");
        countryList.setCountryCode("FI");
        return countryList;
    }

    private static CountryEvent mockCountryEventData() {
        CountryEvent countryEvent = new CountryEvent();
        countryEvent.setCountry(mockCountryData());
        countryEvent.setType(EventType.DATA);
        return countryEvent;
    }

    private static CountryListEvent mockCountryListEventData() {
        CountryListEvent countryEvent = new CountryListEvent();
        countryEvent.setCountry(mockCountryListData());
        countryEvent.setType(EventType.DATA);
        return countryEvent;
    }
}
