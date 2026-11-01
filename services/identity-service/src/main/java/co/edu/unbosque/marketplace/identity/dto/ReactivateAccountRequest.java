package co.edu.unbosque.marketplace.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReactivateAccountRequest(
        @NotBlank @Email String email,
        @NotBlank String password) {
}
