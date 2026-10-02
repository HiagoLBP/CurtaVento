package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.interfaces.http.controller.AutenticacaoController;
import com.curtavento.curta_vento.infrastructure.security.TokenService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(AutenticacaoController.class)

class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authManager;

    @Test
    void deveRetornar403ParaLoginInvalido() throws Exception {
        Map<String, String> dados = new HashMap<>();
        dados.put("email", "admin@curtavento.com");
        dados.put("senha", "senhaerrada");

        // Simulate failed login (Spring Security normally throws BadCredentialsException)
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais invǜlidas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isForbidden());
    }
}
