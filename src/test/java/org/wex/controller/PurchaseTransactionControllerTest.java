package org.wex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.wex.dto.PurchaseOrder;
import org.wex.entity.PurchaseTransaction;
import org.wex.service.PurchaseTransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseTransactionController.class)
class PurchaseTransactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private PurchaseTransactionService purchaseTransactionService;

    @Test
    @WithMockUser()
    void givenPurchaseTransactionPayloadSavesInDb() throws Exception {

        //given
        given(purchaseTransactionService.createPurchaseOrder(
                PurchaseTransaction.builder()
                        .description("Fuel cost")
                        .amount(BigDecimal.valueOf(63.02))
                        .transaction_date(LocalDate.of(2024, 4, 12))
                        .build()
        )).willReturn(
                PurchaseTransaction.builder()
                        .id(1L)
                        .description("Fuel cost")
                        .amount(BigDecimal.valueOf(63.02))
                        .transaction_date(LocalDate.of(2024, 4, 12))
                        .build()
        );

        //when & then
        mvc.perform(MockMvcRequestBuilders.post("/v1/purchase-orders")
                        .with(csrf())
                        .content(asJsonString(
                                PurchaseTransaction.builder()
                                        .description("Fuel cost")
                                        .amount(BigDecimal.valueOf(63.02))
                                        .transaction_date(LocalDate.of(2024, 4, 12))
                                        .build()
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Fuel cost"))
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(63.02)))
                .andExpect(jsonPath("$.transaction_date").value("2024-04-12"));
    }

    @Test
    @WithMockUser()
    void givenInvalidPurchaseTransactionPayload_Validates_Errors() throws Exception {
        //given, when & then
        mvc.perform(MockMvcRequestBuilders.post("/v1/purchase-orders")
                        .with(csrf())
                        .content(asJsonString(
                                PurchaseTransaction.builder()
                                        .description("Fuel cost over 50 chars over 50 chars over 50 chars over 50 chars over 50 chars over 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 charsover 50 chars")
                                        .build()
                        ))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("size must be between 1 and 50"))
                .andExpect(jsonPath("$.amount").value("Amount is mandatory."))
                .andExpect(jsonPath("$.transaction_date").value("Transaction date is mandatory."));
    }

    @Test
    @WithMockUser()
    void givenPurchaseTransactionDetails_getTransactionAmountApplyingExchangeRate() throws Exception {

        //given
        given(purchaseTransactionService.getPurchaseTransactionInTargetCurrency(1, "United Kingdom-Pound")).willReturn(
                PurchaseOrder.builder()
                        .id(1L)
                        .description("Fuel cost")
                        .transactionDate(LocalDate.of(2024, 4, 12))
                        .totalInUsd(BigDecimal.valueOf(60.23))
                        .currencyRecordDate(LocalDate.of(2024, 3, 31))
                        .exchangeRate(BigDecimal.valueOf(0.793))
                        .totalInTargetCurrency(BigDecimal.valueOf(47.76))
                        .targetCountry("United Kingdom")
                        .targetCurrency("Pound")
                        .build()
        );

        //when & then
        mvc.perform(MockMvcRequestBuilders.get("/v1/purchase-orders/1/United Kingdom-Pound")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Fuel cost"))
                .andExpect(jsonPath("$.totalInUsd").value(BigDecimal.valueOf(60.23)))
                .andExpect(jsonPath("$.transactionDate").value("2024-04-12"))
                .andExpect(jsonPath("$.targetCurrency").value("Pound"))
                .andExpect(jsonPath("$.targetCountry").value("United Kingdom"))
                .andExpect(jsonPath("$.currencyRecordDate").value("2024-03-31"))
                .andExpect(jsonPath("$.exchangeRate").value(BigDecimal.valueOf(0.793)))
                .andExpect(jsonPath("$.totalInTargetCurrency").value(BigDecimal.valueOf(47.76)));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
