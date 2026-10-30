package co.edu.unbosque.marketplace.identity.dto;

import co.edu.unbosque.marketplace.identity.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Solo nombre y teléfono son editables; email y role se reciben para rechazar intentos de cambio. */
public record UpdateProfileRequest(
        @NotBlank @Size(max = 150) String fullName,
        @Size(max = 30)
        @Pattern(regexp = "^[+0-9() .-]*$", message = "El teléfono contiene caracteres no válidos")
        String phone,
        String email,
        Role role) {
}
