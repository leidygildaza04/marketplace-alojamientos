package co.edu.unbosque.marketplace.catalog.service;

import co.edu.unbosque.marketplace.catalog.client.AuditClient;
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

    public AlojamientoService(
            AlojamientoRepository repository,
            RequisitosMinimosAlojamientoValidator validator,
            AuditClient auditClient
    ) {
        this.repository = repository;
        this.validator = validator;
        this.auditClient = auditClient;
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

        Alojamiento alojamiento = repository.findById(alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Alojamiento no encontrado"
                ));

        if (!esAdministrador && !usuarioId.equals(alojamiento.getHostId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes activar una publicación que no te pertenece"
            );
        }

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

        Alojamiento alojamientoGuardado = repository.saveAndFlush(alojamiento);

        try {
            auditClient.registrarActivacion(usuarioId, alojamientoGuardado.getId());
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El alojamiento no pudo completar la activación porque no se pudo registrar la auditoría",
                    exception
            );
        }

        return alojamientoGuardado;
    }
}
