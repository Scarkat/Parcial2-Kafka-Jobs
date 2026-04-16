package com.queries.querie.handler.order;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.OrderBlockHandler;
import com.queries.querie.handler.dto.OrderHandlerContext;
import com.queries.querie.repository.OrderRetryJobRepository; 
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

@Service("stepAOrder")
@RequiredArgsConstructor
@Slf4j
public class StepAOrderEndpointHandler extends OrderBlockHandler {

    private final OrderRetryJobRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final JavaMailSender mailSender;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Override
    public void handle(OrderHandlerContext context) {
        ObjectNode rootNode = (ObjectNode) context.getOrderRetryJob().getPayload();
        ObjectNode dataNode = (ObjectNode) rootNode.get("data");

        if (rootNode.has("httpProcessing") && "SUCCESS".equals(rootNode.get("httpProcessing").get("status").asText())) {
            log.info("[ÓRDENES - PASO A] La orden ya había sido procesada exitosamente en HTTP. Saltando...");
            handleNext(context);
            return; 
        }

        log.info("[ÓRDENES - PASO A] Iniciando reintento de HTTP POST para la orden: {}", context.getEntityId());
        
        try {
            log.info("📦 JSON crudo que se enviará al destino de órdenes: {}", dataNode.toString());

            WebClient webClient = webClientBuilder.build();
            webClient.post()
                    .uri(orderServiceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dataNode.toString()) 
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(10)); 

            ObjectNode httpNode = rootNode.putObject("httpProcessing");
            httpNode.put("status", "SUCCESS");
            
            repository.save(context.getOrderRetryJob());
            log.info("[ÓRDENES - PASO A] Orden procesada en HTTP y guardada localmente (Checkpoint).");
            
            handleNext(context);

        } catch (Exception e) {
            log.error("🚨 [ÓRDENES - PASO A] EXPLOSIÓN AL INTENTAR CONSUMIR EL SERVICIO: ", e);
            manejarFallo(context, e.getMessage());
        }
    }

    private void manejarFallo(OrderHandlerContext context, String error) {
        context.setHasError(true);
        var job = context.getOrderRetryJob();
        job.setAttempt(job.getAttempt() + 1);
        job.setNextRunAt(OffsetDateTime.now().plusMinutes(10)); 
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("betirosadoc@outlook.es");
            message.setSubject("⚠️ ALERTA: Fallo en Paso A de la Orden " + context.getEntityId());
            message.setText("Hola,\n\nSe ha detectado un error al intentar procesar la orden en el microservicio destino.\n\n" +
                            "Detalle del error técnico: " + error + "\n\n" +
                            "Reintento #" + job.getAttempt() + " programado para dentro de 10 minutos.");
            
            mailSender.send(message);
            log.info("[ÓRDENES - PASO A] Correo de alerta de fallo enviado.");
        } catch (Exception mailEx) {
            log.error("[ÓRDENES - PASO A] No se pudo enviar el correo de alerta: {}", mailEx.getMessage());
        }
        
        repository.save(job);
    }
}