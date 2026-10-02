package com.curtavento.curta_vento.infrastructure.security;

import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extrairToken(request);

        if (token != null && tokenService.isTokenValido(token)) {
            String email = tokenService.extrairEmail(token);
            String perfil = tokenService.extrairPerfil(token);
            Long usinaId = tokenService.extrairUsinaId(token);
            Long municipioId = tokenService.extrairMunicipioId(token);

            if (email != null && perfil != null) {
                UsuarioEntity usuario = new UsuarioEntity();
                usuario.setEmail(email);
                usuario.setPerfil(perfil);
                
                if (usinaId != null) {
                    UsinaEntity u = new UsinaEntity();
                    u.setId(usinaId);
                    usuario.setUsina(u);
                }
                
                if (municipioId != null) {
                    MunicipioEntity m = new MunicipioEntity();
                    m.setId(municipioId);
                    usuario.setMunicipio(m);
                }

                var permissoes = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + perfil));
                var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, permissoes);

                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String cabecalhoAuth = request.getHeader("Authorization");
        if (cabecalhoAuth != null && cabecalhoAuth.startsWith("Bearer ")) {
            return cabecalhoAuth.substring(7);
        }
        return null;
    }
}