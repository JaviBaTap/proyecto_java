package com.ruta_limpia.rutas_service.controller;

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
import org.springframework.web.server.ResponseStatusException;

import com.ruta_limpia.rutas_service.client.AuthClient;
import com.ruta_limpia.rutas_service.dto.RutasDtos.ParadaRequest;
import com.ruta_limpia.rutas_service.dto.RutasDtos.ResultadoRequest;
import com.ruta_limpia.rutas_service.dto.RutasDtos.UsuarioInfo;
import com.ruta_limpia.rutas_service.model.Parada;
import com.ruta_limpia.rutas_service.service.RutasService;

@RestController 
@RequestMapping ("/rutas")
public class RutasController {
  private final RutasService service;
  private final AuthClient auth;

  public RutasController(RutasService service, AuthClient auth) {
    this.service = service;
    this.auth = auth;
  }

  @GetMapping ("/mi-ruta")
  public List<Parada> miRuta(
      @RequestHeader(value = "Authorization", required = false) String token) {
    UsuarioInfo u = auth.exigirRol(token, "CONDUCTOR");
    if (u.camionId() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El conductor no tiene camión asignado");
    return service.miRuta(u.camionId());
  }

  @PutMapping("/paradas/{id}/resultado")
  public Parada resultado(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable Long id, @RequestBody ResultadoRequest body) {
    UsuarioInfo u = auth.exigirRol(token, "CONDUCTOR");
    return service.registrarResultado(id, u.camionId(), body);
  }

  @DeleteMapping("/paradas/{id}")
  public ResponseEntity<Void> eliminarParada(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable Long id) {
    auth.exigirRol(token, "CONDUCTOR");
    return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  @PostMapping("/paradas")
  public ResponseEntity<Parada> crear(@RequestBody ParadaRequest body) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(body));
  }
}