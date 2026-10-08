package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.infrastructure.security.TokenService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Test
    void deveBloquearAcessoAnonimoAoDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/resumo-geral"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAcessoAoDashboardComTokenValido() throws Exception {
        UsuarioEntity mockUser = new UsuarioEntity();
        mockUser.setEmail("teste@curtavento.com");
        mockUser.setPerfil("USER");
        
        String token = tokenService.gerarToken(mockUser);

        mockMvc.perform(get("/api/dashboard/resumo-geral")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
