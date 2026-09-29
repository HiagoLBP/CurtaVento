package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.usuario.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    // Chave carregada do application.properties — estável entre reinicializações do servidor.
    private final Key CHAVE_SECRETA;

    public TokenService(@Value("${jwt.secret}") String segredo) {
        this.CHAVE_SECRETA = Keys.hmacShaKeyFor(segredo.getBytes());
    }

    // Tempo de validade do token: 2 horas (em milissegundos)
    private static final long TEMPO_VALIDADE = 7200000;

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) // O "dono" do token
                .claim("perfil", usuario.getPerfil()) // Guardamos o perfil (ex: ADMIN) dentro do token
                .claim("usinaId", usuario.getUsina() != null ? usuario.getUsina().getId() : null)
                .claim("municipioId", usuario.getMunicipio() != null ? usuario.getMunicipio().getId() : null)
                .setIssuedAt(new Date()) // Data de criação
                .setExpiration(new Date(System.currentTimeMillis() + TEMPO_VALIDADE)) // Data de validade
                .signWith(CHAVE_SECRETA) // Carimbo criptográfico
                .compact(); // Converte tudo para uma String segura
    }

    // Função para ler o e-mail de dentro do token quando o utilizador faz uma requisição
    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(CHAVE_SECRETA)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extrairPerfil(String token) {
        return Jwts.parserBuilder().setSigningKey(CHAVE_SECRETA).build()
                .parseClaimsJws(token).getBody().get("perfil", String.class);
    }

    public Long extrairUsinaId(String token) {
        return Jwts.parserBuilder().setSigningKey(CHAVE_SECRETA).build()
                .parseClaimsJws(token).getBody().get("usinaId", Long.class);
    }

    public Long extrairMunicipioId(String token) {
        return Jwts.parserBuilder().setSigningKey(CHAVE_SECRETA).build()
                .parseClaimsJws(token).getBody().get("municipioId", Long.class);
    }

    // Função para verificar se o token é verdadeiro e se ainda não expirou
    public boolean isTokenValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(CHAVE_SECRETA).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Se alguém tentou falsificar ou se o tempo expirou, cai aqui
        }
    }
}