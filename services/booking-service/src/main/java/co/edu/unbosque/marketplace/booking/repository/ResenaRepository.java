package co.edu.unbosque.marketplace.booking.repository;

import co.edu.unbosque.marketplace.booking.domain.Resena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    boolean existsByReservaId(Long reservaId);
    List<Resena> findByUserIdOrderByFechaCreacionDesc(Long userId);
    List<Resena> findByAlojamientoIdOrderByFechaCreacionDesc(Long alojamientoId);
    List<Resena> findByAlojamientoIdAndVisibleTrueOrderByFechaCreacionDesc(Long alojamientoId);
    List<Resena> findAllByOrderByFechaCreacionDesc();
    long countByVisibleTrue();

    @Query("select avg(r.calificacion) from Resena r where r.visible = true")
    Double promedioVisible();
}
