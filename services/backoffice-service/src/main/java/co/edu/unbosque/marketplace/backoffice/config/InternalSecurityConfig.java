package co.edu.unbosque.marketplace.backoffice.config;

import co.edu.unbosque.marketplace.common.security.InternalSecretFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalSecurityConfig {

    @Bean
    public InternalSecretFilter internalSecretFilter(
            @Value("${app.internal.secret}") String internalSecret) {
        return new InternalSecretFilter(internalSecret);
    }
}
