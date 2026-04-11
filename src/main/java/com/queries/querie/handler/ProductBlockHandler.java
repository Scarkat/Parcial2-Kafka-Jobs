package com.queries.querie.handler;

import com.queries.querie.handler.dto.ProductHandlerContext;

public abstract class ProductBlockHandler {
    protected ProductBlockHandler next;

    public void setNext(ProductBlockHandler next) {
        this.next = next;
    }

    public abstract void handle(ProductHandlerContext context);

    protected void handleNext(ProductHandlerContext context) {
        if (next != null && !context.isHasError()) {
            next.handle(context);
        }
    }
}