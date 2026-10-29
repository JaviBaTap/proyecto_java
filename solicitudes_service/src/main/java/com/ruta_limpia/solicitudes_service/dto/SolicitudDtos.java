package com.ruta_limpia.solicitudes_service.dto;

public class SolicitudDtos {
  public record CrearSolicitudRequest(String direccion, String tipoResiduo, String horarioPreferido) {}
  public record ActualizarEstadoRequest(String estado, Long camionId) {}
  public record UsuarioInfo(Long id, String nombre, String email, String rol, Long camionId) {}
}