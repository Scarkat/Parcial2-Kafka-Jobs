package com.queries.querie.handler.payment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.queries.querie.handler.PaymentBlockHandler;
import com.queries.querie.handler.dto.PaymentHandlerContext;
import com.queries.querie.repository.PaymentRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StepBSendEmailHandler extends PaymentBlockHandler {

    private final PaymentRetryJobRepository repository;
    private final JavaMailSender mailSender;

    @Override
    public void handle(PaymentHandlerContext context) {
        ObjectNode rootNode = (ObjectNode) context.getPaymentRetryJob().getPayload();
        ObjectNode emailNode = (ObjectNode) rootNode.get("sendEmail");

        if (emailNode.has("status") && "SUCCESS".equals(emailNode.get("status").asText())) {
            log.info("[PASO B] El correo ya fue enviado. Saltando...");
            handleNext(context);
            return; 
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("betirosadoc@outlook.es");
            message.setSubject("Pago " + context.getEntityId() + " creado correctamente");
            message.setText("Hola Betty,\n\nEl pago con ID " + context.getEntityId() + 
                            " se ha procesado y registrado con éxito tras el reintento.");
            
            mailSender.send(message);

            emailNode.put("status", "SUCCESS");
            emailNode.put("message", "Correo enviado exitosamente"); 
            repository.save(context.getPaymentRetryJob());
            
            log.info("[PASO B] Correo enviado a betirosadoc@outlook.es");
            handleNext(context);
            
        } catch (Exception e) {
            log.error("[PASO B] Falló el envío real del correo: {}", e.getMessage());
            manejarFallo(context, e.getMessage());
        }
    }

    private void manejarFallo(PaymentHandlerContext context, String error) {
        context.setHasError(true);
        var job = context.getPaymentRetryJob();
        job.setAttempt(job.getAttempt() + 1);
        job.setNextRunAt(OffsetDateTime.now().plusMinutes(10)); 
        repository.save(job);
    }
}