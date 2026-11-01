package co.edu.unbosque.marketplace.booking.repository;
import co.edu.unbosque.marketplace.booking.domain.Reserva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReservaRepository extends JpaRepository<Reserva,Long> { List<Reserva> findByUserIdAndEstadoAndCheckOutAfter(Long userId,String estado,LocalDate date); }
