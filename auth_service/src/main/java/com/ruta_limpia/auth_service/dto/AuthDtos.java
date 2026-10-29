package com.ruta_limpia.auth_service.dto;

public class AuthDtos {

    public record RegistroRequest(String nombre, String email, String password, String rol, Long camionId) {}
    public record LoginRequest(String email, String password) {}
    public record UsuarioInfo(Long id, String nombre, String email, String rol, Long camionId) {}
}
