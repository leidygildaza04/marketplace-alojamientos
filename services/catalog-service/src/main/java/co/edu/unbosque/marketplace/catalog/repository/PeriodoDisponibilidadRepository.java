package co.edu.unbosque.marketplace.catalog.repository;

import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PeriodoDisponibilidadRepository extends JpaRepository<PeriodoDisponibilidad, Long> {
    List<PeriodoDisponibilidad> findByAlojamientoIdOrderByFechaInicioAsc(Long alojamientoId);
    Optional<PeriodoDisponibilidad> findByIdAndAlojamientoId(Long id, Long alojamientoId);

    boolean existsByAlojamientoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long alojamientoId, LocalDate fechaFin, LocalDate fechaInicio);

    boolean existsByAlojamientoIdAndIdNotAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long alojamientoId, Long periodoId, LocalDate fechaFin, LocalDate fechaInicio);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update PeriodoDisponibilidad p set p.estado = :reservado, p.reservaId = :reservaId "
            + "where p.alojamiento.id = :alojamientoId and p.estado = :disponible "
            + "and p.fechaInicio <= :fechaInicio and p.fechaFin >= :fechaFin")
    int reservarAtomico(@Param("alojamientoId") Long alojamientoId,
            @Param("reservaId") Long reservaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("disponible") PeriodoDisponibilidad.Estado disponible,
            @Param("reservado") PeriodoDisponibilidad.Estado reservado);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update PeriodoDisponibilidad p set p.estado = :disponible, p.reservaId = null "
            + "where p.alojamiento.id = :alojamientoId and p.reservaId = :reservaId "
            + "and p.estado = :reservado")
    int liberarAtomico(@Param("alojamientoId") Long alojamientoId,
            @Param("reservaId") Long reservaId,
            @Param("disponible") PeriodoDisponibilidad.Estado disponible,
            @Param("reservado") PeriodoDisponibilidad.Estado reservado);
}
