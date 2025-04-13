package org.banking.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless API
                .cors(CorsConfigurer::disable)  // Disable CORS configuration (optional, you might want to enable CORS for frontend access)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/v1/login", "/auth/v1/refreshToken", "/auth/v1/signup").permitAll()  // Allow these paths without authentication
                        .anyRequest().authenticated()  // Require authentication for other requests
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session
                .httpBasic(Customizer.withDefaults())  // HTTP Basic authentication (for debugging, you might want to switch to JWT in production)
                .build();
    }
}
