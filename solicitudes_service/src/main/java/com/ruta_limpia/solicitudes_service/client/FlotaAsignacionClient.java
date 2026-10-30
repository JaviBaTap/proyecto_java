package com.ruta_limpia.solicitudes_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.AsignacionResponse;

@Component
public class FlotaAsignacionClient {
    private final RestClient http;

    public FlotaAsignacionClient(@Value("${rutalimpia.flota.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    public AsignacionResponse asignar(Long solicitudId, String direccion, String tipoResiduo, String horarioPreferido) {
        return http.post().uri("/flota/asignaciones")
            .body(new AsignacionRequest(solicitudId, direccion, tipoResiduo, horarioPreferido))
            .retrieve().body(AsignacionResponse.class);
    }

    private record AsignacionRequest(Long solicitudId, String direccion, String tipoResiduo, String horarioPreferido) {}
}
