package com.ruta_limpia.solicitudes_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import com.ruta_limpia.solicitudes_service.service.SolicitudService;

@Component
public class ProcesadorColaAsignacion {
    private static final Logger log = LoggerFactory.getLogger(ProcesadorColaAsignacion.class);
    private final SqsClient sqs;
    private final String queueUrl;
    private final SolicitudService service;

    public ProcesadorColaAsignacion(SqsClient sqs,
            @Value("${rutalimpia.sqs.queue-url}") String queueUrl,
            SolicitudService service) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
        this.service = service;
    }

    @Scheduled(fixedDelayString = "${rutalimpia.sqs.poll-delay-ms:5000}")
    public void procesarMensajes() {
        try {
            var respuesta = sqs.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(10)
                .build());

            for (Message mensaje : respuesta.messages()) {
                try {
                    Long solicitudId = Long.valueOf(mensaje.body());
                    service.procesarAsignacion(solicitudId);
                    sqs.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(mensaje.receiptHandle())
                        .build());
                    log.info("Solicitud {} procesada desde SQS", solicitudId);
                } catch (Exception e) {
                    // No se elimina el mensaje: SQS podrá reintentarlo cuando expire su visibilidad.
                    log.error("No se pudo procesar el mensaje SQS {}", mensaje.messageId(), e);
                }
            }
        } catch (Exception e) {
            // Permite que la aplicación siga funcionando si AWS no está disponible temporalmente.
            log.warn("No se pudo consultar la cola SQS. Revisa URL, región y credenciales: {}", e.getMessage());
        }
    }
}
