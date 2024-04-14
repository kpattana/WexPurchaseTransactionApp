package org.wex.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.wex.dto.ExchangeRateResponse;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ExchangeRateConsumingServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeRateConsumingService exchangeRateConsumingService;

    @Test
    void givenValidPurchaseTransactionData_ShouldCallApiForExchangeRate() {
        //given
        when(restTemplate.getForEntity(anyString(), any())).thenReturn(ResponseEntity.of(Optional.empty()));

        //when
        exchangeRateConsumingService.getExchangeRateFromExternalApi("United Kingdom-Pound", LocalDate.of(2024, 4, 12));

        //then
        verify(restTemplate, times(1))
                .getForEntity(
                        "null&filter=country_currency_desc:eq:United Kingdom-Pound,record_date:gte:2023-10-12",
                        ExchangeRateResponse.class
                );
    }
}
