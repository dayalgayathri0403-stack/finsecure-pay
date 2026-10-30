package com.finsecure.pay;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("FinSecure Pay is running!");
    }

    @PostMapping("/validate")
    public ResponseEntity<TransactionResponse> validateTransaction(
            @Valid @RequestBody TransactionRequest request) {

        if (request.senderAccount().equals(request.receiverAccount())) {
            return ResponseEntity.badRequest().body(
                new TransactionResponse(
                    request.transactionId(),
                    false,
                    "Sender and receiver accounts cannot be the same"
                )
            );
        }

        return ResponseEntity.ok(
            new TransactionResponse(
                request.transactionId(),
                true,
                "Transaction approved successfully"
            )
        );
    }
}