package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.usuario.Usuario;
import com.curtavento.curta_vento.usuario.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    // Este método é executado UMA VEZ a cada requisição (OncePerRequestFilter)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Extrai o token do cabeçalho da requisição
        String token = extrairToken(request);

        // 2. Se o token existir e for válido matematicamente...
        if (token != null && tokenService.isTokenValido(token)) {

            // 3. Descobre quem é o dono do token
            String email = tokenService.extrairEmail(token);
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario != null) {
                // 4. Cria o objeto de autenticação nativo do Spring Security
                var permissoes = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil()));
                var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, permissoes);

                // 5. Salva a autenticação no contexto. O "porteiro" libera a entrada!
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
        }

        // 6. Passa a requisição para o próximo filtro ou para o Controller
        filterChain.doFilter(request, response);
    }

    // Função auxiliar para cortar a palavra "Bearer " e pegar apenas o código
    private String extrairToken(HttpServletRequest request) {
        String cabecalhoAuth = request.getHeader("Authorization");
        if (cabecalhoAuth != null && cabecalhoAuth.startsWith("Bearer ")) {
            return cabecalhoAuth.substring(7);
        }
        return null;
    }
}