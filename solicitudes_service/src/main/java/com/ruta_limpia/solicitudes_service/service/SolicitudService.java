package com.ruta_limpia.solicitudes_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruta_limpia.solicitudes_service.client.ColaAsignacion;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.ActualizarEstadoRequest;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.CrearSolicitudRequest;
import com.ruta_limpia.solicitudes_service.model.SolicitudRetiro;
import com.ruta_limpia.solicitudes_service.repository.SolicitudRepository;

@Service 
public class SolicitudService {
  private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);
  private final SolicitudRepository repo;
  private final ColaAsignacion cola;

  public SolicitudService(SolicitudRepository repo, ColaAsignacion cola) {
    this.repo = repo;
    this.cola = cola;
  }

  public SolicitudRetiro crear(Long usuarioId, CrearSolicitudRequest r) {
    SolicitudRetiro s = new SolicitudRetiro();
    s.setUsuarioId(usuarioId);
    s.setDireccion(r.direccion());
    s.setTipoResiduo(r.tipoResiduo());
    s.setHorarioPreferido(r.horarioPreferido());
    s.setFolio("RL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    s.setEstado("PENDIENTE");
    s.setFechaCreacion(LocalDateTime.now());
    s = repo.save(s);                      
    try {
      cola.encolar(s.getId());             
    } catch (Exception e) {
        log.error("No se pudo encolar la solicitud {}", s.getId(), e);  
    }
    return s;
  }

  public List<SolicitudRetiro> listarDeUsuario(Long usuarioId) {
    return repo.findByUsuarioId(usuarioId);
  }

  public Optional<SolicitudRetiro> buscarPorFolio(String folio, Long usuarioId) {
    return repo.findByFolioAndUsuarioId(folio, usuarioId);
  }

  public Optional<SolicitudRetiro> buscarPorId(Long id) {
    return repo.findById(id);
  }

  public Optional<SolicitudRetiro> actualizarEstado(Long id, ActualizarEstadoRequest r) {
    return repo.findById(id).map(s -> {
      s.setEstado(r.estado());
      if (r.camionId() != null) s.setCamionId(r.camionId());
      return repo.save(s);
    });
  }
}