package com.test.country.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class CountryClient {
    private static final Logger log = LoggerFactory.getLogger(CountryClient.class);

    @Value("${country.client.url}")
    private String countryClientUrl;
    private final WebClient client;

    public CountryClient(WebClient.Builder builder) {
        this.client = builder.baseUrl(countryClientUrl).filters(exchangeFilterFunctions -> {
            exchangeFilterFunctions.add(logRequest());
            exchangeFilterFunctions.add(logResponse());
        }).build();
    }

    public WebClient getClient() {
        return client;
    }

    public String getCountryClientUrl() {
        return countryClientUrl;
    }

    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                //append clientRequest method and url
                clientRequest
                        .headers()
                        .forEach((name, values) -> values.forEach(value -> sb.append(value)));
                log.debug(sb.toString());
            }
            return Mono.just(clientRequest);
        });
    }

    ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Response: \n");
                log.debug(sb.toString());
            }
            return Mono.just(clientResponse);
        });
    }
}
