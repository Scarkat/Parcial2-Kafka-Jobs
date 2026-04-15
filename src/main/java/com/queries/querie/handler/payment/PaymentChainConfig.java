package com.queries.querie.handler.payment;

import com.queries.querie.handler.PaymentBlockHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentChainConfig {

    @Bean
    public PaymentBlockHandler paymentChain(
            StepAPaymentEndpointHandler stepA,
            StepBSendEmailHandler stepB,
            StepCUpdateStatusHandler stepC
    ) {
        stepA.setNext(stepB).setNext(stepC);
        return stepA; 
    }
}