package com.zy.country.convert;

import com.zy.country.data.Country;
import com.zy.country.data.CountryEvent;
import com.zy.country.data.CountryList;
import com.zy.country.data.CountryListEvent;
import com.zy.country.data.ErrorMessage;
import com.zy.country.data.EventType;
import com.zy.country.client.api.CountryDTO;
import com.zy.country.client.api.Flag;
import com.zy.country.client.api.Name;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Converter to convert response data to internal data
 */
public class CountryConverter {

    private CountryConverter() {
        //Util class
    }

    public static Country convertToCountry(CountryDTO countryDTO) {
        if (Objects.isNull(countryDTO) || Objects.isNull(countryDTO.getName())) {
            return null;
        }
        Country country = new Country();
        country.setName(Optional.ofNullable(countryDTO.getName()).map(Name::getCommon).orElse(""));
        country.setCountryCode(countryDTO.getCountryCode());
        List<String> capitals = countryDTO.getCapital();
        if (!CollectionUtils.isEmpty(capitals)) {
            country.setCapital(capitals.get(0));
        }
        country.setPopulation(countryDTO.getPopulation());
        country.setFlagFileUrl(Optional.ofNullable(countryDTO.getFlag()).map(Flag::getPng).orElse(""));
        return country;
    }

    public static CountryList convertToCountryList(CountryDTO countryDTO) {
        if (Objects.isNull(countryDTO) || Objects.isNull(countryDTO.getName())) {
            return null;
        }
        CountryList country = new CountryList();
        country.setName(Optional.ofNullable(countryDTO.getName()).map(Name::getCommon).orElse(""));
        country.setCountryCode(countryDTO.getCountryCode());
        return country;
    }

    public static CountryEvent convertToCountry(CountryDTO countryDTO, ErrorMessage message) {
        CountryEvent countryEvent = new CountryEvent();
        if (isErrorMessageEmpty(message)) {
            countryEvent.setType(EventType.DATA);
        } else {
            countryEvent.setType(EventType.ERROR);
            countryEvent.setError(message);
        }
        countryEvent.setCountry(convertToCountry(countryDTO));
        return countryEvent;
    }

    public static CountryListEvent convertToCountryList(CountryDTO countryDTO, ErrorMessage message) {
        CountryListEvent countryEvent = new CountryListEvent();
        if (isErrorMessageEmpty(message)) {
            countryEvent.setType(EventType.DATA);
        } else {
            countryEvent.setType(EventType.ERROR);
            countryEvent.setError(message);
        }
        countryEvent.setCountry(convertToCountryList(countryDTO));
        return countryEvent;
    }

    private static boolean isErrorMessageEmpty(ErrorMessage message) {
        return message == null || (!StringUtils.hasLength(message.getMessage()) && Objects.isNull(message.getStatus()));

    }
}
