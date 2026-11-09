package co.edu.unbosque.marketplace.booking.repository;

import co.edu.unbosque.marketplace.booking.domain.Resena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByAlojamientoIdAndVisibleTrueOrderByFechaCreacionDesc(
            Long alojamientoId
    );
}
