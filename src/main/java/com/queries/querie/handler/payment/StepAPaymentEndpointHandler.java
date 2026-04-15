package com.queries.querie.handler.payment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.PaymentBlockHandler;
import com.queries.querie.handler.dto.PaymentHandlerContext;
import com.queries.querie.repository.PaymentRetryJobRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class StepAPaymentEndpointHandler extends PaymentBlockHandler {

    private final PaymentRetryJobRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final JavaMailSender mailSender; 
    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Override
    public void handle(PaymentHandlerContext context) {
        ObjectNode rootNode = (ObjectNode) context.getPaymentRetryJob().getPayload();
        ObjectNode dataNode = (ObjectNode) rootNode.get("data");

        if (rootNode.has("httpProcessing") && "SUCCESS".equals(rootNode.get("httpProcessing").get("status").asText())) {
            log.info("[PASO A] El pago ya había sido procesado exitosamente en HTTP. Saltando...");
            handleNext(context);
            return; 
        }

        log.info("[PASO A] Iniciando reintento de HTTP POST para el pago: {}", context.getEntityId());
        
        try {
            log.info("📦 JSON crudo que se enviará al 8082: {}", dataNode.toString());

            WebClient webClient = webClientBuilder.build();
            webClient.post()
                    .uri(paymentServiceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dataNode.toString()) 
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofSeconds(10)); 

            ObjectNode httpNode = rootNode.putObject("httpProcessing");
            httpNode.put("status", "SUCCESS");

            repository.save(context.getPaymentRetryJob());
            log.info("[PASO A] Pago procesado en HTTP y guardado localmente (Checkpoint).");
 
            handleNext(context);

        } catch (Exception e) {
            log.error("🚨 [PASO A] EXPLOSIÓN AL INTENTAR CONSUMIR EL SERVICIO: ", e);
            manejarFallo(context, e.getMessage());
        }
    }

    private void manejarFallo(PaymentHandlerContext context, String error) {
        context.setHasError(true);
        var job = context.getPaymentRetryJob();
        job.setAttempt(job.getAttempt() + 1);
        job.setNextRunAt(OffsetDateTime.now().plusMinutes(10)); 
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("betirosadoc@outlook.es");
            message.setSubject("⚠️ ALERTA: Fallo en Paso A del Pago " + context.getEntityId());
            message.setText("Hola,\n\nSe ha detectado un error al intentar enviar el pago al microservicio destino.\n\n" +
                            "Detalle del error técnico: " + error + "\n\n" +
                            "El orquestador registrará el fallo y programará un nuevo reintento para dentro de 10 minutos (Este será el intento #" + job.getAttempt() + ").");
            
            mailSender.send(message);
            log.info("[PASO A] Correo de alerta de fallo enviado a betirosadoc@outlook.es");
        } catch (Exception mailEx) {
            log.error("[PASO A] No se pudo enviar el correo de alerta por un problema de red: {}", mailEx.getMessage());
        }
        
        repository.save(job);
    }
}