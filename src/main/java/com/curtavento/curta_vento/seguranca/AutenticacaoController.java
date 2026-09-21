package com.curtavento.curta_vento.seguranca;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.curtavento.curta_vento.usuario.Usuario;
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
        // Valida as credenciais
        var tokenLogin = new UsernamePasswordAuthenticationToken(dados.get("email"), dados.get("senha"));
        var autenticacao = authManager.authenticate(tokenLogin);

        // Puxa o usuário e gera o token longo
        Usuario usuario = (Usuario) autenticacao.getPrincipal();
        String tokenJwt = tokenService.gerarToken(usuario);

        // Devolve {"token": "eyJhbG..."} para o Postman
        return ResponseEntity.ok(Map.of("token", tokenJwt));
    }
}