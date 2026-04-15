package com.queries.querie.scheduler;

import com.queries.querie.entities.postgres.PaymentRetryJob;
import com.queries.querie.handler.PaymentBlockHandler;
import com.queries.querie.handler.dto.PaymentHandlerContext;
import com.queries.querie.repository.PaymentRetryJobRepository;
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
public class PaymentRetryScheduler {

    private final PaymentRetryJobRepository repository;
    private final PaymentBlockHandler paymentChain; 

    @Scheduled(fixedDelay = 10000)
    public void processPendingPayments() {
        log.info("⏰ [SCHEDULER] Despertando...");
        
        List<PaymentRetryJob> jobs = repository.findByGlobalStatus("PENDING");
        log.info("🔍 [SCHEDULER] Encontró {} registros con status PENDING en la base de datos.", jobs.size());

        OffsetDateTime ahora = OffsetDateTime.now();
        
        List<PaymentRetryJob> jobsToRun = jobs.stream()
                .filter(job -> {
                    boolean yaEsHora = job.getNextRunAt().isBefore(ahora) || job.getNextRunAt().isEqual(ahora);
                    if (!yaEsHora) {
                        log.warn("⏳ [SCHEDULER] Registro {} ignorado por la hora. Programado para: {} | Hora actual del sistema: {}", 
                                job.getEntityId(), job.getNextRunAt(), ahora);
                    }
                    return yaEsHora;
                })
                .collect(Collectors.toList());

        if (jobsToRun.isEmpty()) {
            log.info("💤 [SCHEDULER] No hay nada listo para procesar en este momento. Volviendo a dormir.");
            return;
        }

        log.info("🚀 [SCHEDULER] Iniciando cadena de responsabilidad para {} pagos...", jobsToRun.size());

        for (PaymentRetryJob job : jobsToRun) {
            log.info("▶️ [SCHEDULER] Enviando entidad {} al Paso A...", job.getEntityId());
            PaymentHandlerContext context = PaymentHandlerContext.builder()
                    .paymentRetryJob(job)
                    .action(job.getAction())
                    .entityId(job.getEntityId())
                    .hasError(false)
                    .build();

            try {
                paymentChain.handle(context);
                log.info("✅ [SCHEDULER] Cadena finalizada para la entidad {}.", job.getEntityId());
            } catch (Exception e) {
                log.error("❌ [SCHEDULER] Error catastrófico ejecutando la cadena: {}", e.getMessage(), e);
            }
        }
    }
}