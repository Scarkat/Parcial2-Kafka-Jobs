package com.queries.querie.handler.order;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.OrderBlockHandler;
import com.queries.querie.handler.dto.OrderHandlerContext;
import com.queries.querie.repository.OrderRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("stepCOrder")
@RequiredArgsConstructor
@Slf4j
public class StepCUpdateStatusHandler extends OrderBlockHandler {

    private final OrderRetryJobRepository repository;

    @Override
    public void handle(OrderHandlerContext context) {
        var job = context.getOrderRetryJob();
        ObjectNode rootNode = (ObjectNode) job.getPayload();
        ObjectNode updateNode = (ObjectNode) rootNode.get("updateRetryJobs");

        if ("SUCCESS".equals(job.getGlobalStatus())) {
            log.info("[ÓRDENES - PASO C] Registro ya completado. No se hace nada.");
            return;
        }

        log.info("[ÓRDENES - PASO C] Actualizando el registro final a SUCCESS para la orden: {}", context.getEntityId());
        
        updateNode.put("status", "SUCCESS");
        updateNode.put("message", "Orden recuperada y procesada correctamente");
        job.setGlobalStatus("SUCCESS");
        
        repository.save(job);
        
        log.info("=== 🚀 CADENA COMPLETADA EXITOSAMENTE PARA LA ORDEN {} ===", context.getEntityId());
    }
}