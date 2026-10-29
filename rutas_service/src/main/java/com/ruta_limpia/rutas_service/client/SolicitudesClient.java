package com.ruta_limpia.rutas_service.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SolicitudesClient {
  private final RestClient http;

  public SolicitudesClient(@Value("${rutalimpia.solicitudes.url}") String baseUrl) {
    this.http = RestClient.builder().baseUrl(baseUrl).build();
  }

  public void actualizarEstado(Long solicitudId, String estado) {
    http.put().uri("/solicitudes/interno/{id}/estado", solicitudId)
      .body(Map.of("estado", estado))
      .retrieve().toBodilessEntity();
  }
}