package br.gov.dataprev.poc.api_contador.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {
    @Value("{jwt.secret}")
    private String jwtSecretBase64;

    public String gerarToken(String login){
        SecretKey chave = Keys.hmacShaKeyFor(jwtSecretBase64.getBytes());

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + 3600000);

        return Jwts.builder()
                .subject(login)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }
}