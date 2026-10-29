package com.ruta_limpia.flota_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruta_limpia.flota_service.client.AuthClient;
import com.ruta_limpia.flota_service.dto.FlotaDtos.AsignacionRequest;
import com.ruta_limpia.flota_service.dto.FlotaDtos.DisponibilidadRequest;
import com.ruta_limpia.flota_service.model.Camion;
import com.ruta_limpia.flota_service.service.FlotaService;

@RestController 
@RequestMapping ("/flota")
public class FlotaController {
    private final FlotaService service;
    private final AuthClient auth;

    public FlotaController(FlotaService service, AuthClient auth) {
        this.service = service;
        this.auth = auth;
    }

    @PostMapping
    public ResponseEntity<Camion> crear(
        @RequestHeader(value = "Authorization", required = false) String token,
        @RequestBody Camion camion) {
        auth.validar(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(camion));
    }

    @GetMapping
    public List<Camion> obtenerTodos(
        @RequestHeader(value = "Authorization", required = false) String token) {
        auth.validar(token);
        return service.obtenerTodos();
    }

    @GetMapping("/disponibles")
    public List<Camion> disponibles(
        @RequestHeader(value = "Authorization", required = false) String token) {
        auth.validar(token);
        return service.disponibles();
    }

    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<Camion> disponibilidad(
        @RequestHeader(value = "Authorization", required = false) String token,
        @PathVariable Long id, @RequestBody DisponibilidadRequest body) {
        auth.validar(token);
        return service.cambiarActivo(id, body.activo())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/asignaciones")
    public Map<String, Long> asignar(@RequestBody AsignacionRequest body) {
        return Map.of("camionId", service.asignar(body).getId());
    }
}