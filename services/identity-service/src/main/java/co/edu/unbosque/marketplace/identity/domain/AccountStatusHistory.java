package co.edu.unbosque.marketplace.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "account_status_history")
public class AccountStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "old_status", nullable = false, length = 20)
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 20)
    private String newStatus;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected AccountStatusHistory() { }

    public AccountStatusHistory(Long userId, boolean oldStatus, boolean newStatus) {
        this.userId = userId;
        this.oldStatus = oldStatus ? "ACTIVE" : "DISABLED";
        this.newStatus = newStatus ? "ACTIVE" : "DISABLED";
        this.changedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
    public Instant getChangedAt() { return changedAt; }
}
