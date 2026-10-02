package com.curtavento.curta_vento.infrastructure.security;

import com.curtavento.curta_vento.infrastructure.persistence.entity.UsuarioEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    private final Key CHAVE_SECRETA;

    public TokenService(@Value("${jwt.secret}") String segredo) {
        this.CHAVE_SECRETA = Keys.hmacShaKeyFor(segredo.getBytes());
    }

    private static final long TEMPO_VALIDADE = 7200000;

    public String gerarToken(UsuarioEntity usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("perfil", usuario.getPerfil())
                .claim("usinaId", usuario.getUsina() != null ? usuario.getUsina().getId() : null)
                .claim("municipioId", usuario.getMunicipio() != null ? usuario.getMunicipio().getId() : null)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TEMPO_VALIDADE))
                .signWith(CHAVE_SECRETA)
                .compact();
    }

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

    public boolean isTokenValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(CHAVE_SECRETA).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}