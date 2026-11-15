package co.edu.unbosque.marketplace.identity.web;

import co.edu.unbosque.marketplace.identity.domain.Role;
import co.edu.unbosque.marketplace.identity.domain.AccountStatusHistory;
import co.edu.unbosque.marketplace.identity.repository.AccountStatusHistoryRepository;
import co.edu.unbosque.marketplace.identity.dto.UserResponse;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import co.edu.unbosque.marketplace.identity.service.AccountServicesClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsersController {

    private final UserRepository users;
    private final AccountStatusHistoryRepository history;
    private final AccountServicesClient accountServices;

    public AdminUsersController(UserRepository users, AccountStatusHistoryRepository history,
            AccountServicesClient accountServices) {
        this.users = users;
        this.history = history;
        this.accountServices = accountServices;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<UserResponse> listar() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @PatchMapping("/{userId}/status")
    @Transactional
    public UserResponse cambiarEstado(@PathVariable Long userId,
            @Valid @RequestBody CambiarEstadoRequest request, Authentication authentication) {
        Long adminId = Long.valueOf(authentication.getName());
        var user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (user.getRole() == Role.ADMIN || userId.equals(adminId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede restringir una cuenta administradora");
        }
        if (user.isActive() == request.active()) {
            return UserResponse.from(user);
        }
        if (!request.active()) {
            int upcoming = accountServices.countFutureConfirmedBookings(userId);
            if (upcoming > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "La cuenta tiene " + upcoming + " reserva(s) confirmada(s) futura(s)");
            }
            accountServices.pauseActiveListings(userId);
        }
        boolean oldStatus = user.isActive();
        user.setActive(request.active());
        users.save(user);
        history.save(new AccountStatusHistory(userId, oldStatus, request.active()));
        return UserResponse.from(user);
    }

    public record CambiarEstadoRequest(@NotNull Boolean active, @NotBlank @Size(max = 500) String motivo) {}
}
