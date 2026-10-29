package com.ruta_limpia.flota_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ruta_limpia.flota_service.model.Camion;

@Repository 
public interface CamionRepository extends JpaRepository<Camion, Long> {
  @Query ("select c from Camion c where c.activo = true "
       + "and c.paradasAsignadas < c.capacidad order by c.id")
  List<Camion> disponibles();
}