
package co.edu.unbosque.marketplace.identity.dto;

import co.edu.unbosque.marketplace.identity.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,

        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s]).+$",
                message = "La contraseña debe incluir mayúscula, minúscula, número y carácter especial"
        )
        String password,

        @NotBlank @Size(max = 150) String fullName,
        @NotNull Role role) {
}
