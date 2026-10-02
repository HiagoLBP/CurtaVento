package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.infrastructure.security.TokenService;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsinaEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.MunicipioEntity;
import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        // Chave secreta mockada para testes (deve ter 256 bits/32 caracteres no minimo para o HS256)
        tokenService = new TokenService("chave-secreta-muito-longa-para-testes-jwt-2026-xyz");
    }

    @Test
    void deveGerarEExtrairTokenComSucesso() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail("admin@curtavento.com");
        usuario.setPerfil("ADMIN");
        
        UsinaEntity usina = new UsinaEntity();
        usina.setId(10L);
        usuario.setUsina(usina);
        
        MunicipioEntity MunicipioEntity = new MunicipioEntity();
        MunicipioEntity.setId(5L);
        usuario.setMunicipio(MunicipioEntity);

        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertTrue(tokenService.isTokenValido(token));
        
        assertEquals("admin@curtavento.com", tokenService.extrairEmail(token));
        assertEquals("ADMIN", tokenService.extrairPerfil(token));
        assertEquals(10L, tokenService.extrairUsinaId(token));
        assertEquals(5L, tokenService.extrairMunicipioId(token));
    }

    @Test
    void deveRetornarInvalidoParaTokenMalFormatado() {
        assertFalse(tokenService.isTokenValido("token.falso.aqui"));
    }
}
