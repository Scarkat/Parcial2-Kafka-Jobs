package com.queries.querie.handler.order;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.OrderBlockHandler;
import com.queries.querie.handler.dto.OrderHandlerContext;
import com.queries.querie.repository.OrderRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service("stepBOrder")
@RequiredArgsConstructor
@Slf4j
public class StepBSendEmailHandler extends OrderBlockHandler {

    private final OrderRetryJobRepository repository;
    private final JavaMailSender mailSender;

    @Override
    public void handle(OrderHandlerContext context) {
        ObjectNode rootNode = (ObjectNode) context.getOrderRetryJob().getPayload();
        ObjectNode emailNode = (ObjectNode) rootNode.get("sendEmail");

        if (emailNode.has("status") && "SUCCESS".equals(emailNode.get("status").asText())) {
            log.info("[ÓRDENES - PASO B] El correo ya fue enviado. Saltando...");
            handleNext(context);
            return; 
        }

        log.info("[ÓRDENES - PASO B] Intentando enviar correo de ÉXITO para la orden: {}", context.getEntityId());
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("betirosadoc@outlook.es");
            message.setSubject("✅ Orden " + context.getEntityId() + " procesada correctamente");
            message.setText("Hola,\n\nLa orden con ID " + context.getEntityId() + 
                            " se ha procesado y registrado con éxito tras el reintento del sistema.");
            
            mailSender.send(message);
            emailNode.put("status", "SUCCESS");
            emailNode.put("message", "Correo de orden enviado exitosamente"); 
            repository.save(context.getOrderRetryJob());
            
            log.info("[ÓRDENES - PASO B] Correo enviado a betirosadoc@outlook.es");
            handleNext(context);
            
        } catch (Exception e) {
            log.error("[ÓRDENES - PASO B] Falló el envío real del correo: {}", e.getMessage());
            manejarFallo(context, e.getMessage());
        }
    }

    private void manejarFallo(OrderHandlerContext context, String error) {
        context.setHasError(true);
        var job = context.getOrderRetryJob();
        job.setAttempt(job.getAttempt() + 1);
        job.setNextRunAt(OffsetDateTime.now().plusMinutes(10)); 
        repository.save(job);
    }
}