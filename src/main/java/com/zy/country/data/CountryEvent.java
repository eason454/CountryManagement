package com.zy.country.data;

import java.io.Serializable;
import java.util.Objects;


/**
 * DTO class for carrying single detailed country data and data type as well as error message
 */
public class CountryEvent implements Serializable {

    private static final long serialVersionUID = 4266948113221622976L;
    private Country country;
    private EventType type;
    private ErrorMessage error;

    public CountryEvent() {
        //Jackson need an empty constructor
    }

    public CountryEvent(Country country, EventType type, ErrorMessage error) {
        this.country = country;
        this.type = type;
        this.error = error;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage error) {
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountryEvent)) return false;
        CountryEvent that = (CountryEvent) o;
        return Objects.equals(country, that.country) && type == that.type && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, type, error);
    }
}
