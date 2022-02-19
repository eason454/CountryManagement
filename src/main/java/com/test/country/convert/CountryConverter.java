package com.test.country.convert;

import com.test.country.api.Country;
import com.test.country.client.api.CountryDTO;
import com.test.country.client.api.Flag;
import com.test.country.client.api.Name;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

public class CountryConverter {

    public static Country convertToCountry(CountryDTO countryDTO) {
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
}
