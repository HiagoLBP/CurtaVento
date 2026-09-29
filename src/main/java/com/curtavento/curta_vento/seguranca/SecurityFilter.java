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

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // Este método é executado UMA VEZ a cada requisição (OncePerRequestFilter)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Extrai o token do cabeçalho da requisição
        String token = extrairToken(request);

        // 2. Se o token existir e for válido matematicamente...
        if (token != null && tokenService.isTokenValido(token)) {

            // 3. Descobre as claims do token (sem bater no banco de dados)
            String email = tokenService.extrairEmail(token);
            String perfil = tokenService.extrairPerfil(token);
            Long usinaId = tokenService.extrairUsinaId(token);
            Long municipioId = tokenService.extrairMunicipioId(token);

            if (email != null && perfil != null) {
                // Cria um usuário "dummy" apenas para passar adiante na requisição
                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setPerfil(perfil);
                
                if (usinaId != null) {
                    com.curtavento.curta_vento.usina.Usina u = new com.curtavento.curta_vento.usina.Usina();
                    u.setId(usinaId);
                    usuario.setUsina(u);
                }
                
                if (municipioId != null) {
                    com.curtavento.curta_vento.municipio.Municipio m = new com.curtavento.curta_vento.municipio.Municipio();
                    m.setId(municipioId);
                    usuario.setMunicipio(m);
                }

                // 4. Cria o objeto de autenticação nativo do Spring Security
                var permissoes = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + perfil));
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