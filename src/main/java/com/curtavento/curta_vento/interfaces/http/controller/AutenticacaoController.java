package com.curtavento.curta_vento.interfaces.http.controller;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import com.curtavento.curta_vento.infrastructure.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager authManager, TokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> dados) {
        var tokenLogin = new UsernamePasswordAuthenticationToken(dados.get("email"), dados.get("senha"));
        var autenticacao = authManager.authenticate(tokenLogin);

        UsuarioEntity usuario = (UsuarioEntity) autenticacao.getPrincipal();
        String tokenJwt = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(Map.of("token", tokenJwt));
    }
}