package co.edu.unbosque.marketplace.booking.repository;

import co.edu.unbosque.marketplace.booking.domain.Reserva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUserId(Long userId);

    List<Reserva> findByUserIdAndEstadoAndCheckOutLessThanEqual(
            Long userId, String estado, LocalDate date);

    List<Reserva> findByUserIdAndEstadoAndCheckOutAfter(
            Long userId, String estado, LocalDate date);

    boolean existsByAlojamientoIdAndEstadoAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
            Long alojamientoId, String estado, LocalDate fechaFin, LocalDate fechaInicio);

    List<Reserva> findByAlojamientoIdAndEstadoAndCheckOutGreaterThanEqual(
            Long alojamientoId, String estado, LocalDate fecha);

    long countByEstado(String estado);

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM reservas", nativeQuery = true)
    BigDecimal sumarIngresosRegistrados();
}
