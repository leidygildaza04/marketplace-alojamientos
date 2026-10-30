package co.edu.unbosque.marketplace.booking.web;

import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/internal/accounts")
public class AccountBookingsController {
 private final ReservaRepository reservas;
 public AccountBookingsController(ReservaRepository reservas){this.reservas=reservas;}
 @GetMapping("/{userId}/future-confirmed-bookings")
 public ResponseEntity<Map<String,Object>> futureConfirmed(@PathVariable Long userId){
  var found=reservas.findByUserIdAndEstadoAndCheckOutAfter(userId,"CONFIRMADA",LocalDate.now());
  var items=found.stream().map(r->Map.of("id",r.getId(),"checkIn",r.getCheckIn(),"checkOut",r.getCheckOut())).toList();
  return ResponseEntity.ok(Map.of("userId",userId,"count",items.size(),"bookings",items));
 }
}
