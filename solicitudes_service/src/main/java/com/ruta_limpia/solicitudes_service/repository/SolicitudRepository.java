package com.ruta_limpia.solicitudes_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ruta_limpia.solicitudes_service.model.SolicitudRetiro;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudRetiro, Long> {
  List<SolicitudRetiro> findByUsuarioId(Long usuarioId);
  Optional<SolicitudRetiro> findByFolioAndUsuarioId(String folio, Long usuarioId);
}