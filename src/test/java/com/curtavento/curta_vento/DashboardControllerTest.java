package com.curtavento.curta_vento;

import com.curtavento.curta_vento.infrastructure.persistence.entity.ImpactoVafEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataImpactoVafRepository;
import com.curtavento.curta_vento.infrastructure.persistence.repository.SpringDataMunicipioRepository;
import com.curtavento.curta_vento.infrastructure.security.TokenService;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SpringDataImpactoVafRepository impactoRepository;

    @Autowired
    private SpringDataMunicipioRepository municipioRepository;

    @Test
    public void deveRetornarImpactoCorretoSalvoNoBancoSemRecalcular() throws Exception {
        // Criar municipio
        MunicipioEntity m = new MunicipioEntity();
        m.setNome("Municipio Teste Dashboard");
        m.setCodigoIbge("1234567");
        m.setVafAnoBase(new BigDecimal("0.05"));
        m = municipioRepository.save(m);

        // Inserir impacto
        ImpactoVafEntity impacto = new ImpactoVafEntity();
        impacto.setMunicipio(m);
        impacto.setAnoReferencia(2026);
        impacto.setEnergiaTotalCortadaMwh(new BigDecimal("100.0"));
        impacto.setPerdaReceitaEstimada(new BigDecimal("15000.0"));
        impacto.setImpactoVafEstimado(new BigDecimal("750.0"));
        impacto.setDataCalculo(java.time.LocalDateTime.now());
        impactoRepository.save(impacto);

        UsuarioEntity user = new UsuarioEntity();
        user.setEmail("admin@test.com");
        user.setPerfil("ADMIN");
        String token = tokenService.gerarToken(user);

        mockMvc.perform(get("/api/dashboard/resumo-geral?ano=2026")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.impactoTotal").value(750.0))
                .andExpect(jsonPath("$.energiaTotal").value(100.0))
                .andExpect(jsonPath("$.perdaReceitaTotal").value(15000.0))
                .andExpect(jsonPath("$.municipios[0].valor").value(750.0));
    }
}
