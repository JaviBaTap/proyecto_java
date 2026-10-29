package com.ruta_limpia.flota_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.ruta_limpia.flota_service.dto.FlotaDtos.ParadaRequest;

@Component
public class RutasClient {
    private final RestClient http;

    public RutasClient(@Value("${rutalimpia.rutas.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void crearParada(ParadaRequest p) {
        http.post().uri("/rutas/paradas").body(p).retrieve().toBodilessEntity();
    }
}