package com.ruta_limpia.auth_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruta_limpia.auth_service.dto.AuthDtos.LoginRequest;
import com.ruta_limpia.auth_service.dto.AuthDtos.RegistroRequest;
import com.ruta_limpia.auth_service.dto.AuthDtos.UsuarioInfo;
import com.ruta_limpia.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
  private final AuthService service;
  public AuthController(AuthService service) { this.service = service; }

  @PostMapping("/registro")
  public ResponseEntity<UsuarioInfo> registro(@RequestBody RegistroRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(r));
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest r) {
    return service.login(r)
      .map(t -> ResponseEntity.ok(Map.of("token", t)))
      .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @GetMapping("/validar")
  public ResponseEntity<UsuarioInfo> validar(
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    return service.validar(authorization)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }
}