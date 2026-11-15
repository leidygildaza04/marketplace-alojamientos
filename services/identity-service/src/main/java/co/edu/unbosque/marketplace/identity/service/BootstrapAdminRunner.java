package co.edu.unbosque.marketplace.identity.service;

import co.edu.unbosque.marketplace.identity.domain.Role;
import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Creates a development/admin account only when explicit credentials are provided. */
@Component
public class BootstrapAdminRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String configuredEmail;
    private final String configuredPassword;

    public BootstrapAdminRunner(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.email:}") String configuredEmail,
            @Value("${app.bootstrap-admin.password:}") String configuredPassword) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.configuredEmail = configuredEmail;
        this.configuredPassword = configuredPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email = configuredEmail.trim().toLowerCase(Locale.ROOT);
        if (email.isBlank() && configuredPassword.isBlank()) {
            return;
        }
        if (email.isBlank() || configuredPassword.isBlank() || configuredPassword.length() < 12) {
            throw new IllegalStateException(
                    "Configure ADMIN_BOOTSTRAP_EMAIL and ADMIN_BOOTSTRAP_PASSWORD (minimum 12 characters)");
        }

        var existing = users.findByEmail(email);
        if (existing.isPresent()) {
            if (existing.get().getRole() != Role.ADMIN) {
                log.error("Bootstrap admin email {} belongs to a non-admin account; choose another email", email);
                return;
            }
            existing.get().setPasswordHash(passwordEncoder.encode(configuredPassword));
            log.info("Bootstrap administrator credentials synchronized for {}", email);
            return;
        }

        users.save(new User(email, passwordEncoder.encode(configuredPassword), "Administrador", Role.ADMIN));
        log.info("Bootstrap administrator created for {}", email);
    }
}
