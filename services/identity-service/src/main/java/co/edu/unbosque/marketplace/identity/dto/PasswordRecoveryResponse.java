package co.edu.unbosque.marketplace.identity.dto;

import java.time.Instant;

public record PasswordRecoveryResponse(String message, String token, Instant expiresAt) { }
