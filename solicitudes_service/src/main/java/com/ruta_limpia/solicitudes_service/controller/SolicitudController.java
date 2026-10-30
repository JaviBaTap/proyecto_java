package com.ruta_limpia.solicitudes_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruta_limpia.solicitudes_service.client.AuthClient;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.ActualizarEstadoRequest;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.CrearSolicitudRequest;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.UsuarioInfo;
import com.ruta_limpia.solicitudes_service.model.SolicitudRetiro;
import com.ruta_limpia.solicitudes_service.model.HistorialEstado;
import com.ruta_limpia.solicitudes_service.service.SolicitudService;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {
  private final SolicitudService service;
  private final AuthClient auth;

  public SolicitudController(SolicitudService service, AuthClient auth) {
    this.service = service;
    this.auth = auth;
  }

  @PostMapping
  public ResponseEntity<SolicitudRetiro> crear(
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestBody CrearSolicitudRequest body) {
    UsuarioInfo u = auth.exigirRol(token, "VECINO");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(u.id(), body));
  }

  @GetMapping
  public List<SolicitudRetiro> misSolicitudes(
      @RequestHeader(value = "Authorization", required = false) String token) {
    UsuarioInfo u = auth.exigirRol(token, "VECINO");
    return service.listarDeUsuario(u.id());
  }

  @GetMapping("/folio/{folio}")
  public ResponseEntity<SolicitudRetiro> porFolio(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable String folio) {
    UsuarioInfo u = auth.exigirRol(token, "VECINO");
    return service.buscarPorFolio(folio, u.id())
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{id}/historial")
  public ResponseEntity<List<HistorialEstado>> historial(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable Long id) {
    UsuarioInfo u = auth.exigirRol(token, "VECINO");
    return service.historialDeUsuario(id, u.id())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable Long id) {
    UsuarioInfo u = auth.exigirRol(token, "VECINO");
    return service.eliminar(id, u.id()) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  @GetMapping("/interno/{id}")
  public ResponseEntity<SolicitudRetiro> internoPorId(@PathVariable Long id) {
    return service.buscarPorId(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/interno/{id}/estado")
  public ResponseEntity<SolicitudRetiro> internoEstado(
      @PathVariable Long id, @RequestBody ActualizarEstadoRequest body) {
    return service.actualizarEstado(id, body)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
}