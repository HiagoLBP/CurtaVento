package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.usina.Usina;
import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.usuario.Usuario;
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
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@curtavento.com");
        usuario.setPerfil("ADMIN");
        
        Usina usina = new Usina();
        usina.setId(10L);
        usuario.setUsina(usina);
        
        Municipio municipio = new Municipio();
        municipio.setId(5L);
        usuario.setMunicipio(municipio);

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
