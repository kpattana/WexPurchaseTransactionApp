package org.wex.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.wex.dto.ExchangeRateResponse;

import java.time.LocalDate;

@Slf4j
@Service
public class ExchangeRateConsumingService {

    private final RestTemplate restTemplate;

    @Value("${fiscal-data.exchange-rate.api}")
    private String fiscalDataApi;

    public ExchangeRateConsumingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ExchangeRateResponse getExchangeRateFromExternalApi(String countryCurrency, LocalDate transactionDate) {
        String filter = String.format("&filter=country_currency_desc:eq:%s,record_date:gte:%s", countryCurrency, transactionDate.minusMonths(6));
        log.info("Filter to be applied: {}", filter);
        ResponseEntity<ExchangeRateResponse> response =  restTemplate.getForEntity(fiscalDataApi + filter, ExchangeRateResponse.class);
        return response.getBody();
    }
}
