package main.java.com.ruta_limpia.api_gateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator rutasGateway(RouteLocatorBuilder builder) {
        return builder.routes()

            // Microservicio de autenticación
            .route("autenticacion", r -> r
                .path("/api/auth/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8081"))

            // Microservicio de solicitudes
            .route("solicitudes", r -> r
                .path("/api/solicitudes/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8082"))

            // Microservicio de flota de camiones
            .route("flota", r -> r
                .path("/api/flota/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8083"))

            // Microservicio de rutas
            .route("rutas", r -> r
                .path("/api/rutas/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8084"))

            .build();
    }
}