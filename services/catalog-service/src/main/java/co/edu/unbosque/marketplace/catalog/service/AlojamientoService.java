package co.edu.unbosque.marketplace.catalog.service;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.dto.CrearAlojamientoRequest;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlojamientoService {

    private final AlojamientoRepository repository;

    public AlojamientoService(AlojamientoRepository repository) {
        this.repository = repository;
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
}
