package com.test.country.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class CountryDTO implements Serializable {
    private static final long serialVersionUID = 4449831113021463501L;

    public CountryDTO() {
        //Jackson need an empty constructor
    }

    private Name name;

    @JsonProperty(value = "cca2")
    private String countryCode;

    private List<String> capital;

    private Long population;

    @JsonProperty("flags")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CountryDTO)) {
            return false;
        }
        CountryDTO that = (CountryDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(countryCode, that.countryCode) && Objects.equals(capital, that.capital) && Objects.equals(population, that.population) && Objects.equals(flag, that.flag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, countryCode, capital, population, flag);
    }
}
