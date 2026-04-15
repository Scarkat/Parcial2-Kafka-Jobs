package com.queries.querie.handler.payment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.PaymentBlockHandler;
import com.queries.querie.handler.dto.PaymentHandlerContext;
import com.queries.querie.repository.PaymentRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StepCUpdateStatusHandler extends PaymentBlockHandler {

    private final PaymentRetryJobRepository repository;

    @Override
    public void handle(PaymentHandlerContext context) {
        var job = context.getPaymentRetryJob();
        ObjectNode rootNode = (ObjectNode) job.getPayload();
        ObjectNode updateNode = (ObjectNode) rootNode.get("updateRetryJobs");

        if ("SUCCESS".equals(job.getGlobalStatus())) {
            log.info("[PASO C] Este registro ya estaba completado de forma global. No se hace nada.");
            return;
        }

        log.info("[PASO C] Actualizando el registro final a SUCCESS para el pago: {}", context.getEntityId());
        
        updateNode.put("status", "SUCCESS");
        updateNode.put("message", "Pago recuperado y procesado correctamente");
        job.setGlobalStatus("SUCCESS");
        repository.save(job);
        
        log.info("=== 🚀 CADENA COMPLETADA EXITOSAMENTE PARA EL PAGO {} ===", context.getEntityId());
    }
}