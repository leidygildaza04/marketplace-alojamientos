package co.edu.unbosque.marketplace.identity.repository;

import co.edu.unbosque.marketplace.identity.domain.AccountStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistory, Long> {
}
