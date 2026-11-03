package co.edu.unbosque.marketplace.catalog.repository;

import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoDisponibilidadRepository extends JpaRepository<PeriodoDisponibilidad, Long> {
    List<PeriodoDisponibilidad> findByAlojamientoIdOrderByFechaInicioAsc(Long alojamientoId);
    Optional<PeriodoDisponibilidad> findByIdAndAlojamientoId(Long id, Long alojamientoId);
}
