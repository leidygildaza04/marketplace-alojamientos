package co.edu.unbosque.marketplace.identity.service;

import co.edu.unbosque.marketplace.identity.domain.AccountStatusHistory;
import co.edu.unbosque.marketplace.identity.domain.User;
import co.edu.unbosque.marketplace.identity.dto.ReactivateAccountRequest;
import co.edu.unbosque.marketplace.identity.repository.AccountStatusHistoryRepository;
import co.edu.unbosque.marketplace.identity.repository.UserRepository;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service public class AccountLifecycleService {
 private final UserRepository users; private final AccountStatusHistoryRepository history; private final PasswordEncoder passwords;
 private final RestClient catalog; private final RestClient booking;
 public AccountLifecycleService(UserRepository users,AccountStatusHistoryRepository history,PasswordEncoder passwords,RestClient.Builder builder, org.springframework.core.env.Environment env){
  this.users=users;this.history=history;this.passwords=passwords;
  this.catalog=builder.baseUrl(env.getProperty("app.services.catalog-url","http://localhost:8082")).build();
  this.booking=builder.baseUrl(env.getProperty("app.services.booking-url","http://localhost:8083")).build();
 }
 @Transactional public void deactivate(Long userId){
  User user=users.findById(userId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"USER_NOT_FOUND","Usuario no encontrado"));
  if(!user.isActive()) throw new ApiException(HttpStatus.CONFLICT,"ACCOUNT_ALREADY_DISABLED","La cuenta ya está desactivada");
  try {
   Map<String,Object> result=booking.get().uri("/internal/accounts/{id}/future-confirmed-bookings",userId).retrieve().body(new ParameterizedTypeReference<>(){});
   int count=((Number)result.getOrDefault("count",0)).intValue();
   if(count>0) throw new ApiException(HttpStatus.CONFLICT,"FUTURE_BOOKINGS_PENDING","No puedes desactivar la cuenta: tienes "+count+" reserva(s) futura(s) confirmada(s) que debes gestionar.");
   catalog.post().uri("/internal/accounts/{id}/pause-listings",userId).retrieve().toBodilessEntity();
  } catch(RestClientException ex){throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,"ACCOUNT_SERVICES_UNAVAILABLE","No fue posible validar reservas o pausar publicaciones. Intenta nuevamente.");}
  user.setActive(false);users.save(user);history.save(new AccountStatusHistory(user.getId(),true,false));
 }
 @Transactional public void reactivate(ReactivateAccountRequest request){
  String email=request.email().trim().toLowerCase(Locale.ROOT);
  User user=users.findByEmail(email).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"INVALID_CREDENTIALS","Correo o contraseña incorrectos"));
  if(!passwords.matches(request.password(),user.getPasswordHash())) throw new ApiException(HttpStatus.UNAUTHORIZED,"INVALID_CREDENTIALS","Correo o contraseña incorrectos");
  if(user.isActive()) throw new ApiException(HttpStatus.CONFLICT,"ACCOUNT_ALREADY_ACTIVE","La cuenta ya está activa");
  user.setActive(true);users.save(user);history.save(new AccountStatusHistory(user.getId(),false,true));
 }
}
