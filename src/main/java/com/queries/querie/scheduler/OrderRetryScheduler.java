package com.queries.querie.scheduler;

import com.queries.querie.entities.postgres.OrderRetryJob;
import com.queries.querie.handler.OrderBlockHandler;
import com.queries.querie.handler.dto.OrderHandlerContext;
import com.queries.querie.repository.OrderRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderRetryScheduler {

    private final OrderRetryJobRepository repository;
    private final OrderBlockHandler orderChain; 

    @Scheduled(fixedDelay = 10000)
    public void processPendingOrders() {
        List<OrderRetryJob> jobs = repository.findByGlobalStatus("PENDING");
        
        if (jobs.isEmpty()) {
            return; 
        }

        OffsetDateTime ahora = OffsetDateTime.now();
        
        List<OrderRetryJob> jobsToRun = jobs.stream()
                .filter(job -> job.getNextRunAt().isBefore(ahora) || job.getNextRunAt().isEqual(ahora))
                .collect(Collectors.toList());

        if (jobsToRun.isEmpty()) {
            return;
        }

        log.info("🚀 [SCHEDULER - ÓRDENES] Iniciando cadena para {} órdenes...", jobsToRun.size());

        for (OrderRetryJob job : jobsToRun) {
            OrderHandlerContext context = OrderHandlerContext.builder()
                    .orderRetryJob(job)
                    .action(job.getAction())
                    .entityId(job.getEntityId())
                    .hasError(false)
                    .build();

            try {
                orderChain.handle(context);
            } catch (Exception e) {
                log.error("❌ [SCHEDULER - ÓRDENES] Error catastrófico ejecutando la cadena: {}", e.getMessage(), e);
            }
        }
    }
}