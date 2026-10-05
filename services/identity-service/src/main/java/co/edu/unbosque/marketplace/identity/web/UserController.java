package co.edu.unbosque.marketplace.identity.web;

import co.edu.unbosque.marketplace.identity.dto.UserResponse;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import co.edu.unbosque.marketplace.identity.service.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    /** Ruta protegida: devuelve al usuario del token (base para HU-03 y HU-05). */
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return users.findById(Long.valueOf(authentication.getName()))
                .map(UserResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "Usuario no encontrado"));
    }
}
