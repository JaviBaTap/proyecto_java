package com.ruta_limpia.rutas_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Parada {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long camionId;
  @Column(unique = true)
  private Long solicitudId;      // una solicitud = una parada
  private String direccion;
  private String tipoResiduo;
  private String horarioPreferido;
  private String estado;         // PENDIENTE | REALIZADA | FALLIDA
  private String observacion;    // motivo si falla
}