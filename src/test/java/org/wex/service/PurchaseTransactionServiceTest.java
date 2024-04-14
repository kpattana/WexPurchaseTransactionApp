package org.wex.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wex.dto.ExchangeRateData;
import org.wex.dto.ExchangeRateResponse;
import org.wex.dto.PurchaseOrder;
import org.wex.entity.PurchaseTransaction;
import org.wex.repository.PurchaseTransactionDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseTransactionServiceTest {
    @Mock
    private PurchaseTransactionDAO purchaseTransactionDAO;

    @Mock
    private ExchangeRateConsumingService exchangeRateConsumingService;

    @InjectMocks
    private PurchaseTransactionService purchaseTransactionService;

    @Test
    void givenValidPurchaseTransactionPayload_SavesData_ReturnSavedRow() {
        //given
        when(purchaseTransactionDAO.saveAndFlush(any())).thenReturn
                (PurchaseTransaction.builder().id(1L).build()
                );
        //when
        PurchaseTransaction purchaseTransaction = purchaseTransactionService
                .createPurchaseOrder(
                        PurchaseTransaction.builder()
                                .description("commodity")
                                .build()
                );
        //then
        assertEquals(purchaseTransaction.getId(), 1L);
    }

    @Test
    void givenValidSavedPurchaseTransactionAndCurrency_calculateAmountApplyingExcangeRate() {
        when(purchaseTransactionDAO.findById(1L)).thenReturn(
                Optional.ofNullable(
                        PurchaseTransaction.builder()
                        .transaction_date(LocalDate.of(2024, 4, 12))
                        .id(1L)
                        .description("Fuel cost")
                        .amount(BigDecimal.valueOf(60.23))
                        .build()
                )
        );
        when(exchangeRateConsumingService.getExchangeRateFromExternalApi("United Kingdom-Pound", LocalDate.of(2024, 4, 12)))
                .thenReturn(
                        new ExchangeRateResponse(List.of(
                              new ExchangeRateData("United Kingdom", "Pound", BigDecimal.valueOf(0.793), LocalDate.of(2024, 3, 31)),
                              new ExchangeRateData("United Kingdom", "Pound", BigDecimal.valueOf(0.783), LocalDate.of(2024, 2, 28)),
                              new ExchangeRateData("United Kingdom", "Pound", BigDecimal.valueOf(0.773), LocalDate.of(2024, 1, 31))
                        ))
                );


        PurchaseOrder purchaseOrder = purchaseTransactionService.getPurchaseTransactionInTargetCurrency(1L, "United Kingdom-Pound");

        assertEquals(purchaseOrder.getId(), 1L);
        assertEquals(purchaseOrder.getDescription(), "Fuel cost");
        assertEquals(0, purchaseOrder.getTransactionDate().compareTo(LocalDate.of(2024, 4, 12)));
        assertEquals(0, purchaseOrder.getTotalInUsd().compareTo(BigDecimal.valueOf(60.23)));
        assertEquals(0, purchaseOrder.getExchangeRate().compareTo(BigDecimal.valueOf(0.793)));
        assertEquals(0, purchaseOrder.getTotalInTargetCurrency().compareTo(BigDecimal.valueOf(47.76)));
        assertEquals(purchaseOrder.getTargetCountry(), "United Kingdom");
        assertEquals(purchaseOrder.getTargetCurrency(), "Pound");
        assertEquals(0, purchaseOrder.getCurrencyRecordDate().compareTo(LocalDate.of(2024, 3, 31)));
    }

    @Test
    void givenInvalidPurchaseTransactionId_throwsException() {
        when(purchaseTransactionDAO.findById(1L)).thenReturn(
                Optional.empty()
        );

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            purchaseTransactionService.getPurchaseTransactionInTargetCurrency(1L, "United Kingdom-Pound");
        });

        String expectedMessage = "Purchase transaction not found for id = 1";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    void givenInvalidCountryCurrency_throwsException() {
        when(purchaseTransactionDAO.findById(1L)).thenReturn(
                Optional.ofNullable(
                        PurchaseTransaction.builder()
                                .transaction_date(LocalDate.of(2024, 4, 12))
                                .id(1L)
                                .description("Fuel cost")
                                .amount(BigDecimal.valueOf(60.23))
                                .build()
                )
        );

        when(exchangeRateConsumingService.getExchangeRateFromExternalApi("United Kingdom-Pound", LocalDate.of(2024, 4, 12)))
                .thenReturn(
                        new ExchangeRateResponse(List.of())
                );

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            purchaseTransactionService.getPurchaseTransactionInTargetCurrency(1L, "United Kingdom-Pound");
        });

        String expectedMessage = "No exchange rate data found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, expectedMessage);
    }
}
