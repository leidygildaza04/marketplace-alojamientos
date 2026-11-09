package co.edu.unbosque.marketplace.catalog.repository;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlojamientoRepository extends
        JpaRepository<Alojamiento, Long>,
        JpaSpecificationExecutor<Alojamiento> {

    List<Alojamiento> findByHostIdAndEstado(Long hostId, String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Alojamiento a WHERE a.id = :id")
    Optional<Alojamiento> findByIdForUpdate(@Param("id") Long id);
}
