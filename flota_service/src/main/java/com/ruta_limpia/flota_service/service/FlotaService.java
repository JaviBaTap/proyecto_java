package com.ruta_limpia.flota_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ruta_limpia.flota_service.client.RutasClient;
import com.ruta_limpia.flota_service.dto.FlotaDtos.AsignacionRequest;
import com.ruta_limpia.flota_service.model.Camion;
import com.ruta_limpia.flota_service.repository.CamionRepository;

import jakarta.transaction.Transactional;

@Service
public class FlotaService {
    private final CamionRepository repo;
    private final RutasClient rutas;

    public FlotaService(CamionRepository repo, RutasClient rutas) {
        this.repo = repo;
        this.rutas = rutas;
    }

    public Camion crear(Camion c) { return repo.save(c); }
    public List<Camion> obtenerTodos() { return repo.findAll(); }
    public List<Camion> disponibles() { return repo.disponibles(); }

    public Optional<Camion> cambiarActivo(Long id, boolean activo) {
        return repo.findById(id).map(c -> { c.setActivo(activo); return repo.save(c); });
    }

    @Transactional
    public Camion asignar(AsignacionRequest r) {
        Camion c = repo.disponibles().stream().findFirst()
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.CONFLICT, "No hay camiones disponibles"));
        rutas.crearParada(new ParadaRequest(c.getId(), r.solicitudId(),
        r.direccion(), r.tipoResiduo(), r.horarioPreferido()));

        c.setParadasAsignadas(c.getParadasAsignadas() + 1);
        return repo.save(c);
    }
}