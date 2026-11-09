package co.edu.unbosque.marketplace.catalog.service;

import co.edu.unbosque.marketplace.catalog.client.AuditClient;

import co.edu.unbosque.marketplace.catalog.client.BookingReviewClient;
import co.edu.unbosque.marketplace.catalog.client.NotificationClient;
import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.dto.CrearAlojamientoRequest;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlojamientoService {

    private final AlojamientoRepository repository;
    private final RequisitosMinimosAlojamientoValidator validator;
    private final AuditClient auditClient;
    private final BookingReviewClient bookingReviewClient;
    private final NotificationClient notificationClient;
    public AlojamientoService(
            AlojamientoRepository repository,
            RequisitosMinimosAlojamientoValidator validator,
            AuditClient auditClient,
            BookingReviewClient bookingReviewClient,
            NotificationClient notificationClient
    ) {
        this.repository = repository;
        this.validator = validator;
        this.auditClient = auditClient;
        this.bookingReviewClient = bookingReviewClient;
        this.notificationClient = notificationClient;
    }

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

        // Utiliza el mismo bloqueo que el proceso de reserva.
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }

        Alojamiento bloqueado = repository.saveAndFlush(alojamiento);

        registrarAuditoria(() -> {
            bookingReviewClient.enviarReservasARevisionManual(alojamientoId);
            notificationClient.notificarBloqueo(
                    alojamiento.getHostId(), alojamientoId, motivo
            );
            auditClient.registrarBloqueo(administradorId, alojamientoId, motivo);
        }, "bloqueo");

        return bloqueado;
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
