
package co.edu.unbosque.marketplace.booking.repository;

import co.edu.unbosque.marketplace.booking.domain.Reserva;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUserIdAndEstadoAndCheckOutAfter(
            Long userId, String estado, LocalDate date);

    boolean existsByAlojamientoIdAndEstadoAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
            Long alojamientoId, String estado, LocalDate fechaFin, LocalDate fechaInicio);

    List<Reserva> findByAlojamientoIdAndEstadoAndCheckOutGreaterThanEqual(
            Long alojamientoId, String estado, LocalDate fecha);

    Optional<Reserva> findByUserIdAndClaveIdempotencia(
            Long userId, String claveIdempotencia);

    List<Reserva> findByUserIdOrderByCheckInDesc(Long userId);

    List<Reserva> findByAlojamientoIdInOrderByCheckInDesc(List<Long> alojamientoIds);
}