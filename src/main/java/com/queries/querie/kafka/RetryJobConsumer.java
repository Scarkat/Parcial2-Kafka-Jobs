package com.queries.querie.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.queries.querie.entities.postgres.OrderRetryJob;
import com.queries.querie.entities.postgres.PaymentRetryJob;
import com.queries.querie.entities.postgres.ProductRetryJob;

import com.queries.querie.repository.OrderRetryJobRepository;
import com.queries.querie.repository.PaymentRetryJobRepository;
import com.queries.querie.repository.ProductRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryJobConsumer {

     private final ProductRetryJobRepository productRepo;
    private final OrderRetryJobRepository orderRepo;
    private final PaymentRetryJobRepository paymentRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @KafkaListener(topics = "${kafka.topic.ordenes}", groupId = "${spring.kafka.consumer.group-id}")
    public void escucharOrden(String message) {
        try {
            JsonNode payloadEnvuelto = buildWrappedPayload(message);
            String entityId = extractId(payloadEnvuelto);

            if (orderRepo.findByEntityIdAndAction(entityId, "CREATE_ORDER") == null) {
                OrderRetryJob job = OrderRetryJob.builder()
                        .entityId(entityId)
                        .payload(payloadEnvuelto)
                        .action("CREATE_ORDER")
                        .build();
                orderRepo.save(job);
                log.info("Orden guardada en retry_jobs: {}", entityId);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje de orden: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topic.productos}", groupId = "${spring.kafka.consumer.group-id}")
    public void escucharProducto(String message) {
        try {
            JsonNode payloadEnvuelto = buildWrappedPayload(message);
            String entityId = extractId(payloadEnvuelto);

            if (productRepo.findByEntityIdAndAction(entityId, "CREATE_PRODUCT") == null) {
                ProductRetryJob job = ProductRetryJob.builder()
                        .entityId(entityId)
                        .payload(payloadEnvuelto)
                        .action("CREATE_PRODUCT")
                        .build();
                productRepo.save(job);
                log.info("Producto guardado en retry_jobs: {}", entityId);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje de producto: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topic.pagos}", groupId = "${spring.kafka.consumer.group-id}")
    public void escucharPago(String message) {
        try {
            JsonNode payloadEnvuelto = buildWrappedPayload(message);
            String entityId = extractId(payloadEnvuelto);

            if (paymentRepo.findByEntityIdAndAction(entityId, "PROCESS_PAYMENT") == null) {
                PaymentRetryJob job = PaymentRetryJob.builder()
                        .entityId(entityId)
                        .payload(payloadEnvuelto)
                        .action("PROCESS_PAYMENT")
                        .build();
                paymentRepo.save(job);
                log.info("Pago guardado en retry_jobs: {}", entityId);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje de pago: {}", e.getMessage());
        }
    }

    private JsonNode buildWrappedPayload(String rawJson) throws Exception {
        ObjectNode rootNode = objectMapper.createObjectNode();
        

        JsonNode dataNode = objectMapper.readTree(rawJson);
        rootNode.set("data", dataNode);

     
        ObjectNode emailNode = objectMapper.createObjectNode();
        emailNode.put("status", "PENDING");
        emailNode.put("message", (String) null);
        rootNode.set("sendEmail", emailNode);

        ObjectNode updateNode = objectMapper.createObjectNode();
        updateNode.put("status", "PENDING");
        updateNode.put("message", (String) null);
        rootNode.set("updateRetryJobs", updateNode);

        return rootNode;
    }

    private String extractId(JsonNode wrappedPayload) {
        return wrappedPayload.path("data").path("id").asText();
    }
}