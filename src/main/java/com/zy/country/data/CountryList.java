package com.zy.country.data;

import java.io.Serializable;
import java.util.Objects;

public class CountryList implements Serializable {
    private static final long serialVersionUID = -1755434536856654536L;
    private String name;
    private String countryCode;

    public CountryList() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CountryList)) {
            return false;
        }
        CountryList that = (CountryList) o;
        return Objects.equals(name, that.name) && Objects.equals(countryCode, that.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, countryCode);
    }
}
