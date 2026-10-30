package co.edu.unbosque.marketplace.identity.service;

import co.edu.unbosque.marketplace.identity.domain.PasswordResetToken;
import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.dto.PasswordRecoveryResponse;
import co.edu.unbosque.marketplace.identity.repository.PasswordResetTokenRepository;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordRecoveryService {
    private static final Logger log = LoggerFactory.getLogger(PasswordRecoveryService.class);
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository users;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;

    public PasswordRecoveryService(UserRepository users, PasswordResetTokenRepository tokens,
                                   PasswordEncoder passwordEncoder) {
        this.users = users; this.tokens = tokens; this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PasswordRecoveryResponse requestRecovery(String email) {
        User user = users.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                        "No existe una cuenta con ese correo"));
        tokens.deleteByUserId(user.getId());
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String rawToken = HexFormat.of().formatHex(bytes);
        Instant expiresAt = Instant.now().plus(TOKEN_TTL);
        tokens.save(new PasswordResetToken(user, sha256(rawToken), expiresAt));
        // Simulación: el token se devuelve para mostrarlo en pantalla; no se envía correo.
        log.info("auth.password_recovery.requested userId={} expiresAt={}", user.getId(), expiresAt);
        return new PasswordRecoveryResponse("Token de recuperación generado. No se envió correo.", rawToken, expiresAt);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken token = tokens.findByTokenHash(sha256(rawToken.trim()))
                .orElseThrow(() -> invalidToken());
        if (!token.isValidAt(Instant.now())) throw invalidToken();
        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        token.markUsed();
        log.info("auth.password_recovery.completed userId={}", user.getId());
    }

    private static ApiException invalidToken() {
        return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_OR_EXPIRED_RESET_TOKEN",
                "El token es inválido, vencido o ya fue utilizado");
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }
}
