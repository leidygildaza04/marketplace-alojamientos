package co.edu.unbosque.marketplace.identity.web;

import co.edu.unbosque.marketplace.identity.dto.UpdateProfileRequest;
import co.edu.unbosque.marketplace.identity.dto.UserResponse;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import co.edu.unbosque.marketplace.identity.service.ApiException;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    /** HU-05: consulta los datos básicos del usuario autenticado. */
    @GetMapping("/me")
    @Transactional(readOnly = true)
    public UserResponse me(Authentication authentication) {
        return users.findById(Long.valueOf(authentication.getName()))
                .map(UserResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Usuario no encontrado"));
    }

    /** HU-05: actualiza únicamente nombre y teléfono. Correo y rol no son editables. */
    @PutMapping("/me")
    @Transactional
    public UserResponse updateMe(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        var user = users.findById(Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Usuario no encontrado"));

        if (request.email() != null && !request.email().isBlank()) {
            String requestedEmail = request.email().trim().toLowerCase(Locale.ROOT);
            if (!requestedEmail.equals(user.getEmail()) && users.existsByEmail(requestedEmail)) {
                throw new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_USED",
                        "El correo ya está asociado a otra cuenta");
            }
            if (!requestedEmail.equals(user.getEmail())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "EMAIL_NOT_EDITABLE",
                        "El correo no se puede modificar desde el perfil");
            }
        }

        if (request.role() != null && request.role() != user.getRole()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ROLE_NOT_EDITABLE",
                    "El rol no se puede modificar desde el perfil");
        }

        user.setFullName(request.fullName().trim());
        user.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone().trim());
        return UserResponse.from(users.save(user));
    }
}
