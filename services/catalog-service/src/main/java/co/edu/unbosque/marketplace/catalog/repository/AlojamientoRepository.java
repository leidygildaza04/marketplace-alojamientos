package co.edu.unbosque.marketplace.catalog.repository;
import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AlojamientoRepository extends JpaRepository<Alojamiento,Long> { List<Alojamiento> findByHostIdAndEstado(Long hostId,String estado); }
