package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.usuario.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> efetuarLogin(@RequestBody DadosLogin dados) {
        // 1. Empacota o e-mail e a senha recebidos num formato que o Spring Security entende
        var tokenProvisorio = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        // 2. O 'manager' vai ao MySQL (usando o nosso AutenticacaoService) verificar a senha
        Authentication authentication = manager.authenticate(tokenProvisorio);

        // 3. Se a senha estiver correta, fabricamos o token JWT
        String tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        // 4. Devolvemos o token como resposta
        return ResponseEntity.ok(tokenJWT);
    }
}