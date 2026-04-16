package com.queries.querie.handler.order;

import com.queries.querie.handler.OrderBlockHandler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderChainConfig {

    @Bean
    public OrderBlockHandler orderChain(
        @Qualifier("stepAOrder") StepAOrderEndpointHandler stepA,
        @Qualifier("stepBOrder") StepBSendEmailHandler stepB,
        @Qualifier("stepCOrder") StepCUpdateStatusHandler stepC) {
        
        stepA.setNext(stepB);
        stepB.setNext(stepC);
        return stepA;
    }
}