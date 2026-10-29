package com.ruta_limpia.rutas_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ruta_limpia.rutas_service.client.SolicitudesClient;
import com.ruta_limpia.rutas_service.dto.RutasDtos.ParadaRequest;
import com.ruta_limpia.rutas_service.dto.RutasDtos.ResultadoRequest;
import com.ruta_limpia.rutas_service.model.Parada;
import com.ruta_limpia.rutas_service.repository.ParadaRepository;

@Service
public class RutasService {
    private final ParadaRepository repo;
    private final SolicitudesClient solicitudes;

    public RutasService(ParadaRepository repo, SolicitudesClient solicitudes) {
        this.repo = repo;
        this.solicitudes = solicitudes;
    }

    public Parada crear(ParadaRequest r) {
        return repo.findBySolicitudId(r.solicitudId()).orElseGet(() -> {
        Parada p = new Parada();
        p.setCamionId(r.camionId());
        p.setSolicitudId(r.solicitudId());
        p.setDireccion(r.direccion());
        p.setTipoResiduo(r.tipoResiduo());
        p.setHorarioPreferido(r.horarioPreferido());
        p.setEstado("PENDIENTE");
        return repo.save(p);
        });
    }

    public List<Parada> miRuta(Long camionId) {
        return repo.findByCamionId(camionId);
    }

    public Parada registrarResultado(Long paradaId, Long camionId, ResultadoRequest r) {
        if (!List.of("REALIZADA", "FALLIDA").contains(r.estado()))
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido");

        Parada p = repo.findById(paradaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parada no existe"));
        if (!p.getCamionId().equals(camionId))     
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esa parada no es de tu camión");

        p.setEstado(r.estado());
        p.setObservacion(r.observacion());
        p = repo.save(p);
        solicitudes.actualizarEstado(p.getSolicitudId(), r.estado());
        return p;
    }
}