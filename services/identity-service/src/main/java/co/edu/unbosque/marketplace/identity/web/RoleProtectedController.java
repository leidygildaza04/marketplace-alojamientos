package co.edu.unbosque.marketplace.identity.web;

import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleProtectedController {

    @GetMapping("/host/dashboard")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    public Map<String, String> hostDashboard() {
        return Map.of("message", "Acceso autorizado para HOST o ADMIN");
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminDashboard() {
        return Map.of("message", "Acceso autorizado para ADMIN");
    }
}