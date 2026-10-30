package com.ruta_limpia.solicitudes_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsConfig {
    @Bean
    SqsClient sqsClient(@Value("${rutalimpia.sqs.region}") String region) {
        return SqsClient.builder().region(Region.of(region)).build();
    }
}
