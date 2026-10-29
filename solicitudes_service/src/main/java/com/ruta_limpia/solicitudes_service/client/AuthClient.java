package com.ruta_limpia.solicitudes_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.UsuarioInfo;

@Component
public class AuthClient {
  private final RestClient http;

  public AuthClient(@Value("${rutalimpia.auth.url}") String baseUrl) {
    this.http = RestClient.builder().baseUrl(baseUrl).build();
  }

  public UsuarioInfo validar(String token) {
    if (token == null)
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta el token");
    try {
      return http.get().uri("/auth/validar")
        .header("Authorization", token)
        .retrieve().body(UsuarioInfo.class);
    } catch (HttpClientErrorException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
    } catch (ResourceAccessException e) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Autenticación no disponible");
    }
  }

  public UsuarioInfo exigirRol(String token, String rol) {
    UsuarioInfo u = validar(token);
    if (!rol.equals(u.rol()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
    return u;
  }
}