package com.queries.querie.handler.dto;

import com.queries.querie.entities.postgres.OrderRetryJob; 
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderHandlerContext {
    private OrderRetryJob orderRetryJob;
    private String action;
    private String entityId;
    private boolean hasError;
}