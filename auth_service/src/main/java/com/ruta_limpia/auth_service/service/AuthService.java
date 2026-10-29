package com.ruta_limpia.auth_service.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ruta_limpia.auth_service.dto.AuthDtos.LoginRequest;
import com.ruta_limpia.auth_service.dto.AuthDtos.RegistroRequest;
import com.ruta_limpia.auth_service.dto.AuthDtos.UsuarioInfo;
import com.ruta_limpia.auth_service.model.Usuario;
import com.ruta_limpia.auth_service.repository.UsuarioRepository;

@Service 
public class AuthService {
  private final UsuarioRepository repo;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
  private final Map<String, Long> sesiones = new ConcurrentHashMap<>();
  
  public AuthService(UsuarioRepository repo) { this.repo = repo; }

  public UsuarioInfo registrar(RegistroRequest r) {
    if (!List.of("VECINO", "CONDUCTOR").contains(r.rol()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido");
    if (repo.existsByEmail(r.email()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
    Usuario u = new Usuario();
    u.setNombre(r.nombre());
    u.setEmail(r.email());
    u.setPassword(encoder.encode(r.password()));
    u.setRol(r.rol());
    u.setCamionId(r.camionId());
    return aInfo(repo.save(u));
  }

  public Optional<String> login(LoginRequest r) {
    return repo.findByEmail(r.email())
      .filter(u -> encoder.matches(r.password(), u.getPassword()))
      .map(u -> {
        String token = UUID.randomUUID().toString();
        sesiones.put(token, u.getId());
        return token;
      });
  }

  public Optional<UsuarioInfo> validar(String authorization) {
    if (authorization == null) return Optional.empty();
    String token = authorization.replace("Bearer ", "").trim();
    return Optional.ofNullable(sesiones.get(token))
      .flatMap(repo::findById)
      .map(this::aInfo);
  }

  private UsuarioInfo aInfo(Usuario u) {
    return new UsuarioInfo(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.getCamionId());
  }
}