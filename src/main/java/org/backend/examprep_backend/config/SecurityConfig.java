package org.backend.examprep_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for testing purposes (re-enable in production)
                .authorizeHttpRequests(authz -> authz
                        // Allow access to register, login, and Swagger UI
                        .requestMatchers("/api/users/register", "/api/users/login",
                                "/swagger-ui.html", "/v3/api-docs/**",
                                "/swagger-ui/**", "/swagger-resources/**",
                                "/webjars/**").permitAll()
                        .anyRequest().authenticated() // All other endpoints require authentication
                )
                .httpBasic(withDefaults()) // Enable basic authentication for testing
                .formLogin(AbstractHttpConfigurer::disable
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password encoding
    }
}
