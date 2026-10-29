package com.ruta_limpia.solicitudes_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter 
@NoArgsConstructor 
public class SolicitudRetiro {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String folio;            
    
    private Long usuarioId;          
    
    private String direccion;
    
    private String tipoResiduo;      
    
    private String horarioPreferido;
    
    private String estado;
    
    private Long camionId;           
    
    private LocalDateTime fechaCreacion;
}