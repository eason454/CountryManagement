package com.test.country.api;

import java.io.Serializable;
import java.util.Objects;

public class Country implements Serializable {
    private static final long serialVersionUID = -2755594536856654536L;
    private String name;
    private String countryCode;
    private String capital;
    private Long population;
    private String flagFileUrl;

    public Country() {
        //Jackson need an empty constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public String getFlagFileUrl() {
        return flagFileUrl;
    }

    public void setFlagFileUrl(String flagFileUrl) {
        this.flagFileUrl = flagFileUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        Country country = (Country) o;
        return Objects.equals(name, country.name) && Objects.equals(countryCode, country.countryCode) && Objects.equals(capital, country.capital) && Objects.equals(population, country.population) && Objects.equals(flagFileUrl, country.flagFileUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, countryCode, capital, population, flagFileUrl);
    }
}
