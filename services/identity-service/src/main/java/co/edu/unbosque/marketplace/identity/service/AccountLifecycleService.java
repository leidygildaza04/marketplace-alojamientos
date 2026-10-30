package co.edu.unbosque.marketplace.identity.service;

import co.edu.unbosque.marketplace.identity.domain.AccountStatusHistory;
import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.dto.ReactivateAccountRequest;
import co.edu.unbosque.marketplace.identity.repository.AccountStatusHistoryRepository;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountLifecycleService {
    private final UserRepository users;
    private final AccountStatusHistoryRepository history;
    private final PasswordEncoder passwords;

    public AccountLifecycleService(UserRepository users, AccountStatusHistoryRepository history,
                                   PasswordEncoder passwords) {
        this.users = users;
        this.history = history;
        this.passwords = passwords;
    }

    @Transactional
    public void deactivate(Long userId) {
        User user = users.findById(userId).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Usuario no encontrado"));
        if (!user.isActive()) {
            throw new ApiException(HttpStatus.CONFLICT, "ACCOUNT_ALREADY_DISABLED", "La cuenta ya está desactivada");
        }
        user.setActive(false);
        users.save(user);
        history.save(new AccountStatusHistory(user.getId(), true, false));
    }

    @Transactional
    public void reactivate(ReactivateAccountRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        User user = users.findByEmail(email).orElseThrow(() ->
                new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Correo o contraseña incorrectos"));
        if (!passwords.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Correo o contraseña incorrectos");
        }
        if (user.isActive()) {
            throw new ApiException(HttpStatus.CONFLICT, "ACCOUNT_ALREADY_ACTIVE", "La cuenta ya está activa");
        }
        user.setActive(true);
        users.save(user);
        history.save(new AccountStatusHistory(user.getId(), false, true));
    }
}
