package com.queries.querie.handler;

import com.queries.querie.handler.dto.OrderHandlerContext;

public abstract class OrderBlockHandler {
    protected OrderBlockHandler next;

    public void setNext(OrderBlockHandler next) {
        this.next = next;
    }

    public abstract void handle(OrderHandlerContext context);

    protected void handleNext(OrderHandlerContext context) {
        if (next != null && !context.isHasError()) {
            next.handle(context);
        }
    }
}