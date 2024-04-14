package org.wex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wex.dto.PurchaseOrder;
import org.wex.entity.PurchaseTransaction;
import org.wex.service.PurchaseTransactionService;

/*
swagger - done
post & get - done
security - done
lombok -- done
should return response entity -done
error handling -done
logging - done
get rid of import * - done
unit test & jacoco -
inMemeory manager parametierised - done
 */
@RestController
@RequestMapping("/v1/purchase-orders")
@SecurityRequirement(name = "login")
@Tag(name = "Purchase Transaction Controller", description = "Api controller to add and fetch purchase transaction with exchange rate applied.")
public class PurchaseTransactionController {
    private final PurchaseTransactionService purchaseTransactionService;

    public PurchaseTransactionController(PurchaseTransactionService purchaseTransactionService) {
        this.purchaseTransactionService = purchaseTransactionService;
    }

    @Operation(summary = "Create new purchase transaction", description = "Create new purchase transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created."),
            @ApiResponse(responseCode = "401", description = "Unauthorised - please provide valid credentials."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PurchaseTransaction> savePurchaseOrder(@RequestBody @Valid PurchaseTransaction purchaseOrderRequest) {
        return new ResponseEntity<>(purchaseTransactionService.createPurchaseOrder(purchaseOrderRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Retrieve purchase transaction", description = "Retrieve purchase transaction for given currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved."),
            @ApiResponse(responseCode = "401", description = "Unauthorised - please provide valid credentials."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping(value = "/{id}/{countryCurrency}", produces = "application/json")
    public ResponseEntity<PurchaseOrder> getPurchaseOrder(
            @PathVariable @Parameter(name = "id", description = "Purchase transaction id", example = "1") final long id,
            @PathVariable @Parameter(name = "countryCurrency", description = "Currency exchange rate to be applied", example = "United Kingdom-Pound") final String countryCurrency) {
        return ResponseEntity.ok().body(purchaseTransactionService.getPurchaseTransactionInTargetCurrency(id, countryCurrency));
    }
}
