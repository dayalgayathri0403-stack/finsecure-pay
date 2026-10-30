
package com.finsecure.pay;

public record TransactionResponse(
    String transactionId,
    boolean approved,
    String message
) {}
