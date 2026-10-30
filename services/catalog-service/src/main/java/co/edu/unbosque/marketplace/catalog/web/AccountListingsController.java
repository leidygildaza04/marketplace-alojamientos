package co.edu.unbosque.marketplace.catalog.web;

import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/internal/accounts")
public class AccountListingsController {
 private final AlojamientoRepository alojamientos;
 public AccountListingsController(AlojamientoRepository alojamientos){this.alojamientos=alojamientos;}
 @PostMapping("/{userId}/pause-listings") @Transactional
 public ResponseEntity<Map<String,Object>> pause(@PathVariable Long userId){
  var active=alojamientos.findByHostIdAndEstado(userId,"ACTIVA");
  active.forEach(a->a.setEstado("PAUSADA"));
  alojamientos.saveAll(active);
  return ResponseEntity.ok(Map.of("userId",userId,"pausedCount",active.size(),"status","OK"));
 }
}
