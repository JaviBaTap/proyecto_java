package com.ruta_limpia.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

  @Value("${rutalimpia.auth.url:http://localhost:8081}")        private String auth;
  @Value("${rutalimpia.solicitudes.url:http://localhost:8082}") private String solicitudes;
  @Value("${rutalimpia.flota.url:http://localhost:8083}")       private String flota;
  @Value("${rutalimpia.rutas.url:http://localhost:8084}")       private String rutas;

  @Bean
  public RouteLocator rutasGateway(RouteLocatorBuilder b) {
    return b.routes()
      // ---- AUTH: solo registro y login ----
      .route("auth", r -> r.path("/api/auth/registro", "/api/auth/login")
        .and().method(HttpMethod.POST)
        .filters(f -> f.stripPrefix(1)).uri(auth))

      // ---- SOLICITUDES (vecino) ----
      .route("solicitudes-base", r -> r.path("/api/solicitudes")
        .and().method(HttpMethod.GET, HttpMethod.POST)
        .filters(f -> f.stripPrefix(1)).uri(solicitudes))
      .route("solicitudes-consulta", r -> r.path("/api/solicitudes/folio/*", "/api/solicitudes/*/historial")
        .and().method(HttpMethod.GET)
        .filters(f -> f.stripPrefix(1)).uri(solicitudes))
      .route("solicitudes-eliminar", r -> r.path("/api/solicitudes/*")
        .and().method(HttpMethod.DELETE)
        .filters(f -> f.stripPrefix(1)).uri(solicitudes))

      // ---- FLOTA ----
      .route("flota-base", r -> r.path("/api/flota")
        .and().method(HttpMethod.GET, HttpMethod.POST)
        .filters(f -> f.stripPrefix(1)).uri(flota))
      .route("flota-disponibles", r -> r.path("/api/flota/disponibles")
        .and().method(HttpMethod.GET)
        .filters(f -> f.stripPrefix(1)).uri(flota))
      .route("flota-disponibilidad", r -> r.path("/api/flota/*/disponibilidad")
        .and().method(HttpMethod.PUT)
        .filters(f -> f.stripPrefix(1)).uri(flota))
      .route("flota-eliminar", r -> r.path("/api/flota/*")
        .and().method(HttpMethod.DELETE)
        .filters(f -> f.stripPrefix(1)).uri(flota))

      // ---- RUTAS (conductor) ----
      .route("rutas-mi-ruta", r -> r.path("/api/rutas/mi-ruta")
        .and().method(HttpMethod.GET)
        .filters(f -> f.stripPrefix(1)).uri(rutas))
      .route("rutas-resultado", r -> r.path("/api/rutas/paradas/*/resultado")
        .and().method(HttpMethod.PUT)
        .filters(f -> f.stripPrefix(1)).uri(rutas))
      .route("rutas-eliminar-parada", r -> r.path("/api/rutas/paradas/*")
        .and().method(HttpMethod.DELETE)
        .filters(f -> f.stripPrefix(1)).uri(rutas))
      .build();
  }
}