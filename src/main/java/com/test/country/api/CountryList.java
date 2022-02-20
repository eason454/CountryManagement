package com.test.country.api;

import java.io.Serializable;

public class CountryList implements Serializable {
    private static final long serialVersionUID = -1755434536856654536L;
    private String name;
    private String countryCode;

    public CountryList() {
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

}
