package com.queries.querie.handler.dto;

import com.queries.querie.entities.postgres.ProductRetryJob;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductHandlerContext {
    private ProductRetryJob productRetryJob;
    private String action;
    private String entityId;
    private boolean hasError;
}