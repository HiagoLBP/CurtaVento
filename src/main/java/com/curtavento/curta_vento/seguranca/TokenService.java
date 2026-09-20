package com.curtavento.curta_vento.seguranca;

import com.curtavento.curta_vento.usuario.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    // Gera uma chave criptográfica forte e única em memória de forma automática.
    // Em sistemas de produção, esta chave costuma vir de uma variável de ambiente.
    private static final Key CHAVE_SECRETA = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Tempo de validade do token: 2 horas (em milissegundos)
    private static final long TEMPO_VALIDADE = 7200000;

    // Função que "fabrica" o crachá digital quando o login é bem sucedido
    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) // O "dono" do token
                .claim("perfil", usuario.getPerfil()) // Guardamos o perfil (ex: ADMIN) dentro do token
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