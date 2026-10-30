
package com.finsecure.pay;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record TransactionRequest(
    @NotBlank String transactionId,
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,
    @Pattern(regexp = "INR|USD", message = "Currency must be INR or USD") String currency,
    @NotBlank String senderAccount,
    @NotBlank String receiverAccount
) {}
