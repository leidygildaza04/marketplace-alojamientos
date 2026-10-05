package co.edu.unbosque.marketplace.identity.dto;

import co.edu.unbosque.marketplace.identity.domain.Role;

public record AuthResponse(String token, Role role, long expiresInMinutes) {
}
