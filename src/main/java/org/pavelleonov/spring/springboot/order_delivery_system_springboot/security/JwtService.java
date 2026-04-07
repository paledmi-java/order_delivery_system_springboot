package org.pavelleonov.spring.springboot.order_delivery_system_springboot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // Перегенерировать в yaml
    private final Key key = Keys.hmacShaKeyFor(
            "my-super-secret-key-my-super-secret-key".getBytes(StandardCharsets.UTF_8)
    );

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    public String extractUsernameIfValid(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if(claims.getExpiration().after(new Date())){
                return claims.getSubject();
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
}
