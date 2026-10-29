package com.ruta_limpia.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ruta_limpia.auth_service.model.Usuario;

@Repository 
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmail(String email);
  boolean existsByEmail(String email);
}
