package co.edu.unbosque.marketplace.booking.config;

import co.edu.unbosque.marketplace.booking.security.JwtAuthFilter;
import co.edu.unbosque.marketplace.booking.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        // APIs authenticate only with explicit Bearer headers; no browser auth cookies or sessions exist.
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/internal/**")) // NOSONAR: API stateless con JWT en cabecera; sin cookies ni sesion, CSRF no aplica
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/internal/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/alojamientos/*/resenas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas/mias").hasRole("GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas/anfitrion/*").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers("/api/v1/reservas/**").hasRole("GUEST")
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
