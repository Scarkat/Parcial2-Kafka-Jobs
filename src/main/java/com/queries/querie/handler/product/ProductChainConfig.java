package com.queries.querie.handler.product;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.queries.querie.handler.ProductBlockHandler;

@Configuration
public class ProductChainConfig {
    @Bean
    public ProductBlockHandler productChain(
            @Qualifier("stepAProduct") StepAProductEndpointHandler stepA,
            @Qualifier("stepBProduct") StepBSendEmailHandler stepB, 
            @Qualifier("stepCProduct") StepCUpdateStatusHandler stepC) {
        
        stepA.setNext(stepB);
        stepB.setNext(stepC);
        return stepA;
    }
}