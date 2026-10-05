package co.edu.unbosque.marketplace.identity.service;

import co.edu.unbosque.marketplace.identity.domain.Role;
import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.dto.AuthResponse;
import co.edu.unbosque.marketplace.identity.dto.LoginRequest;
import co.edu.unbosque.marketplace.identity.dto.RegisterRequest;
import co.edu.unbosque.marketplace.identity.dto.UserResponse;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import co.edu.unbosque.marketplace.identity.security.JwtService;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** HU-01: registro con selección de rol (solo HUÉSPED o ANFITRIÓN). */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ROLE_NOT_ALLOWED",
                    "El rol ADMIN no se puede elegir al registrarse");
        }
        String email = normalize(request.email());
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_USED",
                    "Ya existe una cuenta con ese correo");
        }
        User user = new User(email, passwordEncoder.encode(request.password()),
                request.fullName().trim(), request.role());
        User saved = users.save(user);
        log.info("auth.register userId={} role={}", saved.getId(), saved.getRole());
        return UserResponse.from(saved);
    }

    /** HU-02: inicio de sesión. Devuelve un JWT con el rol del usuario. */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = users.findByEmail(normalize(request.email()))
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> {
                    log.warn("auth.login.failed");
                    return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                            "Correo o contraseña incorrectos");
                });
        if (!user.isActive()) {
            log.warn("auth.login.blocked userId={}", user.getId());
            throw new ApiException(HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED", "La cuenta está desactivada");
        }
        log.info("auth.login userId={} role={}", user.getId(), user.getRole());
        return new AuthResponse(jwtService.generate(user), user.getRole(), jwtService.getExpirationMinutes());
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
