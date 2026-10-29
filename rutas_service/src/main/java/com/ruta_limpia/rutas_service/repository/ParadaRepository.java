package com.ruta_limpia.rutas_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ruta_limpia.rutas_service.model.Parada;

@Repository 
public interface ParadaRepository extends JpaRepository<Parada, Long> {
  List<Parada> findByCamionId(Long camionId);
  Optional<Parada> findBySolicitudId(Long solicitudId);
}