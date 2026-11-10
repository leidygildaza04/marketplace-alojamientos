package co.edu.unbosque.marketplace.catalog.service;

import co.edu.unbosque.marketplace.catalog.client.AuditClient;
import co.edu.unbosque.marketplace.catalog.client.BookingReviewClient;
import co.edu.unbosque.marketplace.catalog.client.BookingResenaClient;
import co.edu.unbosque.marketplace.catalog.client.NotificationClient;
import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.domain.ServicioAdicional;
import co.edu.unbosque.marketplace.catalog.dto.BusquedaAlojamientoRequest;
import co.edu.unbosque.marketplace.catalog.dto.CrearAlojamientoRequest;
import co.edu.unbosque.marketplace.catalog.dto.DetalleAlojamientoResponse;
import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadResponse;
import co.edu.unbosque.marketplace.catalog.dto.ResultadoBusquedaAlojamientoResponse;
import co.edu.unbosque.marketplace.catalog.dto.ResumenResenasResponse;
import co.edu.unbosque.marketplace.catalog.dto.ServicioAdicionalRequest;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoSpecification;
import co.edu.unbosque.marketplace.catalog.repository.PeriodoDisponibilidadRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlojamientoService {

    private final AlojamientoRepository repository;
    private final PeriodoDisponibilidadRepository periodoDisponibilidadRepository;
    private final RequisitosMinimosAlojamientoValidator validator;
    private final AuditClient auditClient;
    private final BookingReviewClient bookingReviewClient;
    private final BookingResenaClient bookingResenaClient;
    private final NotificationClient notificationClient;

    public AlojamientoService(
            AlojamientoRepository repository,
            PeriodoDisponibilidadRepository periodoDisponibilidadRepository,
            RequisitosMinimosAlojamientoValidator validator,
            AuditClient auditClient,
            BookingReviewClient bookingReviewClient,
            BookingResenaClient bookingResenaClient,
            NotificationClient notificationClient
    ) {
        this.repository = repository;
        this.periodoDisponibilidadRepository = periodoDisponibilidadRepository;
        this.validator = validator;
        this.auditClient = auditClient;
        this.bookingReviewClient = bookingReviewClient;
        this.bookingResenaClient = bookingResenaClient;
        this.notificationClient = notificationClient;
    }

    // SCRUM-72: BUSQUEDA DE ALOJAMIENTOS CON FILTROS

    @Transactional(readOnly = true)
    public List<ResultadoBusquedaAlojamientoResponse> buscar(
            BusquedaAlojamientoRequest filtros) {

        if (filtros == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los filtros de búsqueda son obligatorios"
            );
        }

        if ((filtros.fechaInicio() == null) != (filtros.fechaFin() == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las fechas de inicio y fin deben enviarse juntas"
            );
        }

        if (filtros.fechaInicio() != null
                && !filtros.fechaInicio().isBefore(filtros.fechaFin())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio debe ser anterior a la fecha de fin"
            );
        }

        if (filtros.capacidad() != null && filtros.capacidad() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La capacidad debe ser mayor que cero"
            );
        }

        if (filtros.precioMinimo() != null
                && filtros.precioMinimo().signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio mínimo no puede ser negativo"
            );
        }

        if (filtros.precioMaximo() != null
                && filtros.precioMaximo().signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio máximo no puede ser negativo"
            );
        }

        if (filtros.precioMinimo() != null
                && filtros.precioMaximo() != null
                && filtros.precioMinimo().compareTo(filtros.precioMaximo()) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio mínimo no puede superar el precio máximo"
            );
        }

        return repository.findAll(AlojamientoSpecification.conFiltros(filtros))
                .stream()
                .map(ResultadoBusquedaAlojamientoResponse::desde)
                .toList();
    }

    // SCRUM-24: CONSULTAR DETALLE DE PUBLICACION

    @Transactional(readOnly = true)
    public DetalleAlojamientoResponse consultarDetalle(Long alojamientoId) {

        Alojamiento alojamiento = repository.findById(alojamientoId)
                .filter(a -> "ACTIVA".equals(a.getEstado()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alojamiento no encontrado"
                ));

        List<PeriodoDisponibilidadResponse> disponibilidad =
                periodoDisponibilidadRepository
                        .findByAlojamientoIdOrderByFechaInicioAsc(alojamientoId)
                        .stream()
                        .map(PeriodoDisponibilidadResponse::desde)
                        .toList();

        ResumenResenasResponse resumenResenas;

        try {
            resumenResenas = bookingResenaClient.consultarResenas(alojamientoId);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible consultar las reseñas del alojamiento",
                    exception
            );
        }

        if (resumenResenas == null) {
            resumenResenas = new ResumenResenasResponse(null, 0, List.of());
        }

        return new DetalleAlojamientoResponse(
                alojamiento.getId(),
                alojamiento.getTitulo(),
                alojamiento.getDescripcion(),
                alojamiento.getDireccion(),
                alojamiento.getCiudad(),
                alojamiento.getPrecioNoche(),
                alojamiento.getCapacidad(),
                alojamiento.getEstado(),
                alojamiento.getTipoAlojamiento(),
                alojamiento.getReglas(),
                alojamiento.getPoliticaCancelacion(),
                alojamiento.getServicios(),
                alojamiento.getImagenes(),
                disponibilidad,
                resumenResenas.promedio(),
                resumenResenas.cantidad(),
                resumenResenas.resenas() == null
                        ? List.of()
                        : resumenResenas.resenas()
        );
    }

    // SCRUM-75: CREAR ALOJAMIENTO CON TARIFAS Y SERVICIOS ADICIONALES

    @Transactional
    public Alojamiento crear(Long hostId, CrearAlojamientoRequest request) {

        Alojamiento alojamiento = new Alojamiento(
                hostId,
                request.titulo(),
                request.descripcion(),
                request.direccion(),
                request.ciudad(),
                request.precioNoche(),
                request.capacidad()
        );

        alojamiento.actualizarDatosComplementarios(
                request.tipoAlojamiento(),
                request.reglas(),
                request.politicaCancelacion(),
                request.servicios(),
                request.imagenes()
        );

        actualizarTarifasDesdeRequest(alojamiento, request);

        return repository.save(alojamiento);
    }

    @Transactional
    public Alojamiento activar(
            Long usuarioId,
            boolean esAdministrador,
            Long alojamientoId
    ) {

        Alojamiento alojamiento = buscarYAutorizar(
                usuarioId,
                esAdministrador,
                alojamientoId
        );

        List<String> camposFaltantes =
                validator.obtenerCamposFaltantes(alojamiento);

        if (!camposFaltantes.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede activar. Campos faltantes: "
                            + String.join(", ", camposFaltantes)
            );
        }

        try {
            alojamiento.cambiarEstadoA("ACTIVA");
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage(),
                    exception
            );
        }

        Alojamiento alojamientoGuardado =
                repository.saveAndFlush(alojamiento);

        registrarAuditoria(
                () -> auditClient.registrarActivacion(
                        usuarioId,
                        alojamientoGuardado.getId()
                ),
                "activación"
        );

        return alojamientoGuardado;
    }

    // SCRUM-75: EDITAR ALOJAMIENTO Y SUS TARIFAS

    @Transactional
    public Alojamiento editar(
            Long usuarioId,
            boolean esAdministrador,
            Long alojamientoId,
            CrearAlojamientoRequest request
    ) {

        Alojamiento alojamiento = buscarYAutorizar(
                usuarioId,
                esAdministrador,
                alojamientoId
        );

        if ("BLOQUEADA".equals(alojamiento.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede editar una publicación bloqueada"
            );
        }

        alojamiento.actualizarDatos(
                request.titulo(),
                request.descripcion(),
                request.direccion(),
                request.ciudad(),
                request.precioNoche(),
                request.capacidad()
        );

        actualizarTarifasDesdeRequest(alojamiento, request);

        Alojamiento alojamientoGuardado =
                repository.saveAndFlush(alojamiento);

        registrarAuditoria(
                () -> auditClient.registrarEdicion(
                        usuarioId,
                        alojamientoGuardado.getId()
                ),
                "edición"
        );

        return alojamientoGuardado;
    }

    @Transactional
    public Alojamiento pausar(
            Long usuarioId,
            boolean esAdministrador,
            Long alojamientoId
    ) {

        Alojamiento alojamiento = buscarYAutorizarConBloqueo(
                usuarioId,
                esAdministrador,
                alojamientoId
        );

        try {
            alojamiento.cambiarEstadoA("PAUSADA");
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage(),
                    exception
            );
        }

        Alojamiento alojamientoGuardado =
                repository.saveAndFlush(alojamiento);

        registrarAuditoria(
                () -> auditClient.registrarPausa(
                        usuarioId,
                        alojamientoGuardado.getId()
                ),
                "pausa"
        );

        return alojamientoGuardado;
    }

    @Transactional
    public Alojamiento bloquear(
            Long administradorId,
            boolean esAdministrador,
            Long alojamientoId,
            String motivo
    ) {

        if (!esAdministrador) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo un administrador puede bloquear publicaciones"
            );
        }

        Alojamiento alojamiento =
                buscarYAutorizarConBloqueo(administradorId, true, alojamientoId);

        try {
            alojamiento.bloquear(motivo);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage()
            );
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage()
            );
        }

        Alojamiento bloqueado = repository.saveAndFlush(alojamiento);

        registrarAuditoria(() -> {
            bookingReviewClient.enviarReservasARevisionManual(alojamientoId);
            notificationClient.notificarBloqueo(
                    alojamiento.getHostId(),
                    alojamientoId,
                    motivo
            );
            auditClient.registrarBloqueo(
                    administradorId,
                    alojamientoId,
                    motivo
            );
        }, "bloqueo");

        return bloqueado;
    }

    // SCRUM-75: ACTUALIZAR TARIFA DE LIMPIEZA Y SERVICIOS ADICIONALES

    private void actualizarTarifasDesdeRequest(
            Alojamiento alojamiento,
            CrearAlojamientoRequest request
    ) {

        BigDecimal tarifaLimpieza = request.tarifaLimpieza() != null
                ? request.tarifaLimpieza()
                : alojamiento.getTarifaLimpieza();

        List<ServicioAdicional> serviciosAdicionales =
                request.serviciosAdicionales() != null
                        ? convertirServiciosAdicionales(request.serviciosAdicionales())
                        : new ArrayList<>(alojamiento.getServiciosAdicionales());

        alojamiento.actualizarTarifas(
                tarifaLimpieza,
                serviciosAdicionales
        );
    }

    private List<ServicioAdicional> convertirServiciosAdicionales(
            List<ServicioAdicionalRequest> servicios
    ) {

        if (servicios == null) {
            return List.of();
        }

        return servicios.stream()
                .map(servicio -> {

                    if (servicio == null
                            || servicio.nombre() == null
                            || servicio.nombre().isBlank()
                            || servicio.precio() == null
                            || servicio.precio().compareTo(BigDecimal.ZERO) < 0) {

                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Los servicios adicionales deben tener nombre y precio válido"
                        );
                    }

                    return new ServicioAdicional(
                            servicio.nombre().trim(),
                            servicio.precio()
                    );
                })
                .toList();
    }

    private Alojamiento buscarYAutorizar(
            Long usuarioId,
            boolean esAdministrador,
            Long alojamientoId
    ) {

        Alojamiento alojamiento = repository.findById(alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alojamiento no encontrado"
                ));

        validarAutorizacion(usuarioId, esAdministrador, alojamiento);

        return alojamiento;
    }

    private Alojamiento buscarYAutorizarConBloqueo(
            Long usuarioId,
            boolean esAdministrador,
            Long alojamientoId
    ) {

        Alojamiento alojamiento = repository.findByIdForUpdate(alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alojamiento no encontrado"
                ));

        validarAutorizacion(usuarioId, esAdministrador, alojamiento);

        return alojamiento;
    }

    private void validarAutorizacion(
            Long usuarioId,
            boolean esAdministrador,
            Alojamiento alojamiento
    ) {

        if (!esAdministrador
                && (usuarioId == null || !usuarioId.equals(alojamiento.getHostId()))) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para modificar esta publicación"
            );
        }
    }

    private void registrarAuditoria(
            Runnable operacion,
            String nombreOperacion
    ) {

        try {
            operacion.run();
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo completar la operación porque falló "
                            + "el registro de auditoría de " + nombreOperacion,
                    exception
            );
        }
    }
}