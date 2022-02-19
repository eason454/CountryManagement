package com.test.country.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CountryDTO {

    public CountryDTO() {
    }

    private Name name;

    @JsonProperty(value = "cca2")
    private String countryCode;

    private List<String> capital;

    private Long population;

    private Flag flag;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<String> getCapital() {
        return capital;
    }

    public void setCapital(List<String> capital) {
        this.capital = capital;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }
}
