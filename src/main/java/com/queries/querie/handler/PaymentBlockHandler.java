package com.queries.querie.handler;

import com.queries.querie.handler.dto.PaymentHandlerContext;

public abstract class PaymentBlockHandler {
    
    protected PaymentBlockHandler next;

    public PaymentBlockHandler setNext(PaymentBlockHandler next) {
        this.next = next;
        return this.next;
    }

    public abstract void handle(PaymentHandlerContext context);

    public void handleNext(PaymentHandlerContext context) {
        // Si hay error, cortamos la cadena inmediatamente
        if (next != null && !context.isHasError()) {
            next.handle(context);
        }
    }
}