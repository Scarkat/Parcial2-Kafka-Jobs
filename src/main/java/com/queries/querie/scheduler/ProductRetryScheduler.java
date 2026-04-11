package com.queries.querie.scheduler;

import com.queries.querie.entities.postgres.ProductRetryJob;
import com.queries.querie.handler.ProductBlockHandler;
import com.queries.querie.handler.dto.ProductHandlerContext;
import com.queries.querie.repository.ProductRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRetryScheduler {

    private final ProductRetryJobRepository repository;
    private final ProductBlockHandler productChain; 

    @Scheduled(fixedDelay = 10000)
    public void processPendingProducts() {
        List<ProductRetryJob> jobs = repository.findByGlobalStatus("PENDING");
        OffsetDateTime ahora = OffsetDateTime.now();
        
        jobs.stream()
            .filter(job -> job.getNextRunAt().isBefore(ahora) || job.getNextRunAt().isEqual(ahora))
            .forEach(job -> {
                log.info("🚀 [SCHEDULER - PRODUCTOS] Procesando: {}", job.getEntityId());
                ProductHandlerContext context = ProductHandlerContext.builder()
                        .productRetryJob(job)
                        .entityId(job.getEntityId())
                        .hasError(false)
                        .build();
                try {
                    productChain.handle(context);
                } catch (Exception e) {
                    log.error("❌ Error en cadena de productos: {}", e.getMessage());
                }
            });
    }
}