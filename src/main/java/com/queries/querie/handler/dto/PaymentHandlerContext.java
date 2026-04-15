package com.queries.querie.handler.dto;

import com.queries.querie.entities.postgres.PaymentRetryJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHandlerContext {
    private PaymentRetryJob paymentRetryJob;
    private String action;
    private String entityId;
    private boolean hasError;
}