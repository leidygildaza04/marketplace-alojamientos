package co.edu.unbosque.marketplace.catalog.service;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadRequest;
import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadResponse;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import co.edu.unbosque.marketplace.catalog.repository.PeriodoDisponibilidadRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PeriodoDisponibilidadService {

    private final AlojamientoRepository alojamientos;
    private final PeriodoDisponibilidadRepository periodos;

    public PeriodoDisponibilidadService(AlojamientoRepository alojamientos,
            PeriodoDisponibilidadRepository periodos) {
        this.alojamientos = alojamientos;
        this.periodos = periodos;
    }

    @Transactional(readOnly = true)
    public List<PeriodoDisponibilidadResponse> listar(Long alojamientoId) {
        verificarAlojamiento(alojamientoId);
        return periodos.findByAlojamientoIdOrderByFechaInicioAsc(alojamientoId)
                .stream().map(PeriodoDisponibilidadResponse::desde).toList();
    }

    public PeriodoDisponibilidadResponse crear(Long alojamientoId, PeriodoDisponibilidadRequest request) {
        Alojamiento alojamiento = verificarAlojamiento(alojamientoId);
        validarFechas(request);
        if (periodos.existsByAlojamientoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                alojamientoId, request.fechaFin(), request.fechaInicio())) {
            throw conflictoSuperposicion();
        }
        PeriodoDisponibilidad periodo = new PeriodoDisponibilidad(
                alojamiento, request.fechaInicio(), request.fechaFin());
        return PeriodoDisponibilidadResponse.desde(periodos.save(periodo));
    }

    public PeriodoDisponibilidadResponse editar(Long alojamientoId, Long periodoId,
            PeriodoDisponibilidadRequest request) {
        PeriodoDisponibilidad periodo = buscarPeriodo(alojamientoId, periodoId);
        validarFechas(request);
        if (periodos.existsByAlojamientoIdAndIdNotAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                alojamientoId, periodoId, request.fechaFin(), request.fechaInicio())) {
            throw conflictoSuperposicion();
        }
        periodo.actualizarFechas(request.fechaInicio(), request.fechaFin());
        return PeriodoDisponibilidadResponse.desde(periodos.save(periodo));
    }

    public void eliminar(Long alojamientoId, Long periodoId) {
        PeriodoDisponibilidad periodo = buscarPeriodo(alojamientoId, periodoId);
        periodos.delete(periodo);
    }

    private void validarFechas(PeriodoDisponibilidadRequest request) {
        if (request == null || request.fechaInicio() == null || request.fechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Las fechas de inicio y fin son obligatorias");
        }
        LocalDate inicio = request.fechaInicio();
        LocalDate fin = request.fechaFin();
        if (!inicio.isBefore(fin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }

    private ResponseStatusException conflictoSuperposicion() {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                "El periodo se superpone con otro periodo del mismo alojamiento");
    }

    private Alojamiento verificarAlojamiento(Long alojamientoId) {
        return alojamientos.findById(alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El alojamiento no existe"));
    }

    private PeriodoDisponibilidad buscarPeriodo(Long alojamientoId, Long periodoId) {
        verificarAlojamiento(alojamientoId);
        return periodos.findByIdAndAlojamientoId(periodoId, alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El periodo no existe para este alojamiento"));
    }
}
