package org.wex.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wex.dto.ExchangeRateData;
import org.wex.dto.ExchangeRateResponse;
import org.wex.dto.PurchaseOrder;
import org.wex.entity.PurchaseTransaction;
import org.wex.repository.PurchaseTransactionDAO;

import java.math.RoundingMode;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class PurchaseTransactionService {
    private final PurchaseTransactionDAO purchaseTransactionDAO;
    private final ExchangeRateConsumingService exchangeRateConsumingService;

    public PurchaseTransactionService(PurchaseTransactionDAO purchaseTransactionDAO, ExchangeRateConsumingService exchangeRateConsumingService) {
        this.purchaseTransactionDAO = purchaseTransactionDAO;
        this.exchangeRateConsumingService = exchangeRateConsumingService;
    }

    @Transactional
    public PurchaseTransaction createPurchaseOrder(PurchaseTransaction request) {
        log.info("Attempting to save purchase transaction: {}", request.toString());
        return purchaseTransactionDAO.saveAndFlush(request);
    }

    public PurchaseOrder getPurchaseTransactionInTargetCurrency(final long id, final String targetCountryCurrency) {
        PurchaseTransaction purchaseTransaction = purchaseTransactionDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Purchase transaction not found for id = %s", id)));

        log.info("Querying exchange rate api for details: {}", purchaseTransaction.toString());
        ExchangeRateResponse response = exchangeRateConsumingService.getExchangeRateFromExternalApi(targetCountryCurrency, purchaseTransaction.getTransaction_date());

        return response.data().stream()
                .min((r1, r2) -> r2.record_date().compareTo(r1.record_date()))
                .map(ex -> toPurchaseOrder(purchaseTransaction, ex))
                .orElseThrow(() -> new NoSuchElementException("No exchange rate data found."));
    }

    private PurchaseOrder toPurchaseOrder(PurchaseTransaction purchaseTransaction, ExchangeRateData ex) {

        return PurchaseOrder.builder()
                .id(purchaseTransaction.getId())
                .description(purchaseTransaction.getDescription())
                .totalInUsd(purchaseTransaction.getAmount())
                .transactionDate(purchaseTransaction.getTransaction_date())
                .targetCurrency(ex.currency())
                .targetCountry(ex.country())
                .exchangeRate(ex.exchange_rate())
                .totalInTargetCurrency(purchaseTransaction.getAmount().multiply(ex.exchange_rate()).setScale(2, RoundingMode.HALF_UP))
                .currencyRecordDate(ex.record_date())
                .build();
    }
}
