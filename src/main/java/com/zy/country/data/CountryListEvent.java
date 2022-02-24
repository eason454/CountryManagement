package com.zy.country.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO class for carrying country list data and data type as well as error message
 */
public class CountryListEvent implements Serializable {
    private static final long serialVersionUID = 32685957552553761L;
    private CountryList country;
    private EventType type;
    private ErrorMessage error;

    public CountryListEvent() {
        //Jackson need an empty constructor
    }

    public CountryList getCountry() {
        return country;
    }

    public void setCountry(CountryList country) {
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof CountryListEvent)) {
            return false;
        }
        CountryListEvent that = (CountryListEvent) o;
        return Objects.equals(country, that.country) && type == that.type && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, type, error);
    }
}
