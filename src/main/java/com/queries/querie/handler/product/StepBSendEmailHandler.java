package com.queries.querie.handler.product;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.ProductBlockHandler;
import com.queries.querie.handler.dto.ProductHandlerContext;
import com.queries.querie.repository.ProductRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service("stepBProduct")
@RequiredArgsConstructor
@Slf4j
public class StepBSendEmailHandler extends ProductBlockHandler {

    private final ProductRetryJobRepository repository;
    private final JavaMailSender mailSender;

    @Override
    public void handle(ProductHandlerContext context) {
        ObjectNode rootNode = (ObjectNode) context.getProductRetryJob().getPayload();
        ObjectNode emailNode = (ObjectNode) rootNode.get("sendEmail");

        if (emailNode.has("status") && "SUCCESS".equals(emailNode.get("status").asText())) {
            log.info("[PRODUCTOS - PASO B] Correo ya enviado. Saltando...");
            handleNext(context);
            return; 
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("reaa020215@gmail.com");
            message.setSubject("✅ Producto " + context.getEntityId() + " actualizado");
            message.setText("El producto con ID " + context.getEntityId() + " se procesó correctamente.");
            
            mailSender.send(message);

            emailNode.put("status", "SUCCESS");
            repository.save(context.getProductRetryJob());
            
            log.info("[PRODUCTOS - PASO B] Correo enviado exitosamente.");
            handleNext(context);
            
        } catch (Exception e) {
            log.error("[PRODUCTOS - PASO B] Error al enviar correo: {}", e.getMessage());
            manejarFallo(context);
        }
    }

    private void manejarFallo(ProductHandlerContext context) {
        context.setHasError(true);
        var job = context.getProductRetryJob();
        job.setAttempt(job.getAttempt() + 1);
        job.setNextRunAt(OffsetDateTime.now().plusMinutes(10)); 
        repository.save(job);
    }
}