package com.ruta_limpia.flota_service.dto;

public class FlotaDtos {
  public record AsignacionRequest(Long solicitudId, String direccion, String tipoResiduo, String horarioPreferido) {}
  public record ParadaRequest(Long camionId, Long solicitudId, String direccion, String tipoResiduo, String horarioPreferido) {}
  public record DisponibilidadRequest(Boolean activo) {}
  public record UsuarioInfo(Long id, String nombre, String email, String rol, Long camionId) {}
}