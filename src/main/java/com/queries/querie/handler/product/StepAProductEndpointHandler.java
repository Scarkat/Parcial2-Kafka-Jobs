package com.queries.querie.handler.product;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.ProductBlockHandler;
import com.queries.querie.handler.dto.ProductHandlerContext;
import com.queries.querie.repository.ProductRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service("stepAProduct")
@RequiredArgsConstructor
@Slf4j
public class StepAProductEndpointHandler extends ProductBlockHandler {

    private final ProductRetryJobRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final JavaMailSender mailSender;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Override
    public void handle(ProductHandlerContext context) {
        ObjectNode rootNode = (ObjectNode) context.getProductRetryJob().getPayload();
        ObjectNode dataNode = (ObjectNode) rootNode.get("data");

        if (rootNode.has("httpProcessing") && "SUCCESS".equals(rootNode.get("httpProcessing").get("status").asText())) {
            log.info("[PRODUCTOS - PASO A] Procesado previamente. Saltando...");
            handleNext(context);
            return; 
        }

        try {
            log.info("📦 Enviando actualización de producto: {}", context.getEntityId());

            webClientBuilder.build().post()
                    .uri(productServiceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dataNode.toString()) 
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(10)); 

            rootNode.putObject("httpProcessing").put("status", "SUCCESS");
            repository.save(context.getProductRetryJob());
            
            handleNext(context);

        } catch (Exception e) {
            log.error("🚨 [PRODUCTOS - PASO A] Error: ", e);
            manejarFallo(context, e.getMessage());
        }
    }

    private void manejarFallo(ProductHandlerContext context, String error) {
        context.setHasError(true);
        var job = context.getProductRetryJob();
        job.setAttempt(job.getAttempt() + 1);
        job.setNextRunAt(OffsetDateTime.now().plusMinutes(10)); 
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("reaa020215@gmail.com");
            message.setSubject("⚠️ ALERTA: Fallo en Producto " + context.getEntityId());
            message.setText("Error técnico: " + error);
            mailSender.send(message);
        } catch (Exception ignore) {}
        
        repository.save(job);
    }
}