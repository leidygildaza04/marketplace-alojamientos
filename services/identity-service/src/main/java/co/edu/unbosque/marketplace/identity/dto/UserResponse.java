package co.edu.unbosque.marketplace.identity.dto;

import co.edu.unbosque.marketplace.identity.domain.Role;
import co.edu.unbosque.marketplace.identity.domain.User;

public record UserResponse(Long id, String email, String fullName, String phone, Role role, boolean active) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(),
                user.getPhone(), user.getRole(), user.isActive());
    }
}
