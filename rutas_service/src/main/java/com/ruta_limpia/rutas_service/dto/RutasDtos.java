package com.ruta_limpia.rutas_service.dto;

public class RutasDtos {
  public record ParadaRequest(Long camionId, Long solicitudId, String direccion, String tipoResiduo, String horarioPreferido) {}
  public record ResultadoRequest(String estado, String observacion) {}  
  public record UsuarioInfo(Long id, String nombre, String email, String rol, Long camionId) {}
}