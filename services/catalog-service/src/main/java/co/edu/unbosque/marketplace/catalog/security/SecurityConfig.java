
package co.edu.unbosque.marketplace.catalog.security;

import co.edu.unbosque.marketplace.common.security.InternalSecretFilter;
import org.springframework.beans.factory.annotation.Value;
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
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            @Value("${app.internal.secret}") String internalSecret
    ) throws Exception {

        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/internal/**")
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/alojamientos")
                    .hasRole("HOST")

                .requestMatchers(HttpMethod.POST, "/api/alojamientos/*/activar")
                    .hasAnyRole("HOST", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/alojamientos/*")
                    .hasAnyRole("HOST", "ADMIN")

                .requestMatchers(HttpMethod.PATCH, "/api/alojamientos/*/pausar")
                    .hasAnyRole("HOST", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/alojamientos/*/bloquear")
                    .hasRole("ADMIN")

                .requestMatchers("/api/v1/**", "/internal/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                )
            )
            .addFilterBefore(
                new InternalSecretFilter(internalSecret),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(
                new JwtAuthFilter(jwtService),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
