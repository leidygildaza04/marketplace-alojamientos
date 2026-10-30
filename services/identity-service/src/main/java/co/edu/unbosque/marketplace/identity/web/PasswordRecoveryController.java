package co.edu.unbosque.marketplace.identity.web;

import co.edu.unbosque.marketplace.identity.dto.PasswordRecoveryRequest;
import co.edu.unbosque.marketplace.identity.dto.PasswordRecoveryResponse;
import co.edu.unbosque.marketplace.identity.dto.PasswordResetRequest;
import co.edu.unbosque.marketplace.identity.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordRecoveryController {
    private final PasswordRecoveryService recovery;
    public PasswordRecoveryController(PasswordRecoveryService recovery) { this.recovery = recovery; }

    @PostMapping("/password/recovery")
    public PasswordRecoveryResponse requestRecovery(@Valid @RequestBody PasswordRecoveryRequest request) {
        return recovery.requestRecovery(request.email());
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        recovery.resetPassword(request.token(), request.newPassword());
    }
}
