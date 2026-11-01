package co.edu.unbosque.marketplace.identity.web;

import co.edu.unbosque.marketplace.identity.dto.ReactivateAccountRequest;
import co.edu.unbosque.marketplace.identity.service.AccountLifecycleService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountLifecycleController {
    private final AccountLifecycleService lifecycle;

    public AccountLifecycleController(AccountLifecycleService lifecycle) {
        this.lifecycle = lifecycle;
    }

    @PostMapping("/users/me/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> deactivate(Authentication authentication) {
        lifecycle.deactivate(Long.valueOf(authentication.getName()));
        return Map.of("message", "Cuenta desactivada correctamente", "status", "DISABLED");
    }

    @PostMapping("/auth/reactivate")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> reactivate(@Valid @RequestBody ReactivateAccountRequest request) {
        lifecycle.reactivate(request);
        return Map.of("message", "Cuenta reactivada correctamente", "status", "ACTIVE");
    }
}
