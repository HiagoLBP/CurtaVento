package com.curtavento.curta_vento.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    // Configuração principal das regras de trânsito HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desativa a proteção CSRF, pois em APIs REST com JWT não sofremos desse tipo de ataque
                .csrf(csrf -> csrf.disable()) // <-- ISTO É OBRIGATÓRIO PARA APIS REST
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Liberta a rota de Login e criação de utilizadores
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        
                        // Rotas de leitura do ONS e PLD
                        .requestMatchers(HttpMethod.GET, "/api/pld/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ons/**").permitAll()
                        
                        // Proteção de regras administrativas (RBAC) - Controle de Acesso
                        .requestMatchers(HttpMethod.POST, "/api/usinas/**", "/api/municipios/**", "/api/dashboard/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usinas/**", "/api/municipios/**", "/api/dashboard/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usinas/**", "/api/municipios/**", "/api/dashboard/**").hasRole("ADMIN")

                        // Liberta as rotas internas que geram a interface do Swagger e frontend
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/", "/index.html", "/*.html", "/*.css", "/*.js", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        
                        // Exige autenticação para qualquer outra requisição
                        .anyRequest().authenticated()
                )
                // Coloca o nosso filtro de JWT antes do filtro padrão do Spring Security
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Expõe o gestor de autenticação para podermos usá-lo no Controller de Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Ensina ao Spring como criptografar as senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
