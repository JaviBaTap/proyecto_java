package com.ruta_limpia.solicitudes_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class ColaAsignacion {
  private final SqsClient sqs;
  private final String queueUrl;

  public ColaAsignacion(@Value("${rutalimpia.sqs.region}") String region,
                        @Value("${rutalimpia.sqs.queue-url}") String queueUrl) {
    this.sqs = SqsClient.builder().region(Region.of(region)).build();
    this.queueUrl = queueUrl;
  }

  public void encolar(Long solicitudId) {
    sqs.sendMessage(SendMessageRequest.builder()
      .queueUrl(queueUrl)
      .messageBody(String.valueOf(solicitudId))
      .build());
  }
}