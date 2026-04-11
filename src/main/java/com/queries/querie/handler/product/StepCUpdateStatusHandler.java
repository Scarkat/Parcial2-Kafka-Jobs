package com.queries.querie.handler.product;

import com.queries.querie.handler.ProductBlockHandler;
import com.queries.querie.handler.dto.ProductHandlerContext;
import com.queries.querie.repository.ProductRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("stepCProduct")
@RequiredArgsConstructor
@Slf4j
public class StepCUpdateStatusHandler extends ProductBlockHandler {

    private final ProductRetryJobRepository repository;

    @Override
    public void handle(ProductHandlerContext context) {
        var job = context.getProductRetryJob();
        
        if ("SUCCESS".equals(job.getGlobalStatus())) return;

        log.info("[PRODUCTOS - PASO C] Finalizando registro para Producto: {}", context.getEntityId());
        
        job.setGlobalStatus("SUCCESS");
        repository.save(job);
        
        log.info("=== 🚀 CADENA COMPLETADA PARA PRODUCTO {} ===", context.getEntityId());
    }
}