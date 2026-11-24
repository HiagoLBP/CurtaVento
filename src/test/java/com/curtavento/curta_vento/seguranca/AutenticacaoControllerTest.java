package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import com.curtavento.curta_vento.infrastructure.security.SecurityConfig;
import com.curtavento.curta_vento.infrastructure.security.TokenService;
import com.curtavento.curta_vento.interfaces.http.controller.AutenticacaoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutenticacaoController.class)
@Import(SecurityConfig.class)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authManager;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void deveRetornar401ParaLoginInvalido() throws Exception {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@curtavento.com\",\"senha\":\"senhaerrada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void deveRetornar200ComTokenParaLoginValido() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail("admin@curtavento.com");
        usuario.setPerfil("ADMIN");

        var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenService.gerarToken(usuario)).thenReturn("jwt-token-valido");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@curtavento.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-valido"));
    }
}
