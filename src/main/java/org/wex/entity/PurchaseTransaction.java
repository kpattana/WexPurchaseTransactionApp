package org.wex.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "PurchaseTransaction")
@Getter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Purchase Transaction Description(Max length 50)", example = "Fuel", nullable = true)
    @NotNull(message = "Description is mandatory.")
    @Size(min = 1, max = 50)
    private String description;

    @Schema(description = "Purchase Transaction amount in USD", example = "60.24", nullable = false)
    @NotNull(message = "Amount is mandatory.")
    @Min(value = 0L, message = "Amount has to be valid positive value.")
    private BigDecimal amount;

    @Schema(description = "Purchase Transaction date", example = "2024-02-28", nullable = false)
    @NotNull(message = "Transaction date is mandatory.")
    private LocalDate transaction_date;
}
