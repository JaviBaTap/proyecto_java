package com.ruta_limpia.solicitudes_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruta_limpia.solicitudes_service.client.ColaAsignacion;
import com.ruta_limpia.solicitudes_service.client.FlotaAsignacionClient;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.AsignacionResponse;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.ActualizarEstadoRequest;
import com.ruta_limpia.solicitudes_service.dto.SolicitudDtos.CrearSolicitudRequest;
import com.ruta_limpia.solicitudes_service.model.SolicitudRetiro;
import com.ruta_limpia.solicitudes_service.model.HistorialEstado;
import com.ruta_limpia.solicitudes_service.repository.SolicitudRepository;

@Service 
public class SolicitudService {
  private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);
  private final SolicitudRepository repo;
  private final ColaAsignacion cola;
  private final FlotaAsignacionClient flota;

  public SolicitudService(SolicitudRepository repo, ColaAsignacion cola, FlotaAsignacionClient flota) {
    this.repo = repo;
    this.cola = cola;
    this.flota = flota;
  }

  public SolicitudRetiro crear(Long usuarioId, CrearSolicitudRequest r) {
    SolicitudRetiro s = new SolicitudRetiro();
    s.setUsuarioId(usuarioId);
    s.setDireccion(r.direccion());
    s.setTipoResiduo(r.tipoResiduo());
    s.setHorarioPreferido(r.horarioPreferido());
    s.setFolio("RL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    s.setEstado("PENDIENTE_ASIGNACION");
    s.setFechaCreacion(LocalDateTime.now());
    agregarHistorial(s, null, s.getEstado(), "Solicitud recibida y registrada");
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

  public Optional<List<HistorialEstado>> historialDeUsuario(Long solicitudId, Long usuarioId) {
    return repo.findById(solicitudId)
        .filter(s -> s.getUsuarioId().equals(usuarioId))
        .map(SolicitudRetiro::getHistoriales);
  }

  public Optional<SolicitudRetiro> buscarPorFolio(String folio, Long usuarioId) {
    return repo.findByFolioAndUsuarioId(folio, usuarioId);
  }

  public Optional<SolicitudRetiro> buscarPorId(Long id) {
    return repo.findById(id);
  }

  public Optional<SolicitudRetiro> actualizarEstado(Long id, ActualizarEstadoRequest r) {
    return repo.findById(id).map(s -> {
      String anterior = s.getEstado();
      s.setEstado(r.estado());
      if (r.camionId() != null) s.setCamionId(r.camionId());
      if (anterior == null || !anterior.equals(r.estado())) {
        agregarHistorial(s, anterior, r.estado(), "Estado actualizado por el proceso de RutaLimpia");
      }
      return repo.save(s);
    });
  }

  public void procesarAsignacion(Long solicitudId) {
    SolicitudRetiro s = repo.findById(solicitudId)
        .orElseThrow(() -> new IllegalArgumentException("No existe la solicitud " + solicitudId));
    if (s.getCamionId() != null || "COMPLETADA".equals(s.getEstado()) || "FALLIDA".equals(s.getEstado())) return;
    AsignacionResponse respuesta = flota.asignar(s.getId(), s.getDireccion(), s.getTipoResiduo(), s.getHorarioPreferido());
    actualizarEstado(s.getId(), new ActualizarEstadoRequest("ASIGNADA", respuesta.camionId()));
  }

  public boolean eliminar(Long id, Long usuarioId) {
    return repo.findById(id).filter(s -> s.getUsuarioId().equals(usuarioId)).map(s -> {
      repo.delete(s);
      return true;
    }).orElse(false);
  }

  private void agregarHistorial(SolicitudRetiro solicitud, String anterior, String nuevo, String observacion) {
    HistorialEstado h = new HistorialEstado();
    h.setEstadoAnterior(anterior);
    h.setEstadoNuevo(nuevo);
    h.setFechaCambio(LocalDateTime.now());
    h.setObservacion(observacion);
    h.setSolicitud(solicitud);
    solicitud.getHistoriales().add(h);
  }
}