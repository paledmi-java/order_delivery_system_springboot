package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.unit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.InvalidTokenException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.JwtProperties;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.JwtService;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceUnitTest {

    private JwtService jwtService;
    private JwtProperties jwtProperties = new JwtProperties();
    private Key key;

    @BeforeEach
    void setUp() {
        String secret =
                Base64.getEncoder()
                        .encodeToString
                                ("B5/wuDRUFYtV7mfWqgV3gTjCyQ9eA7wlFvuNGgxmiYs=".getBytes());

        jwtProperties.setSecret(secret);
        jwtProperties.setAccessTokenExpirationMs(900000L);
        jwtProperties.setRefreshTokenExpirationMs(604800000L);

        jwtService = new JwtService(jwtProperties);
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecret()));

    }

    @Test
    void generateAccessToken_ShouldGenerateValidAccessToken() {
        //given
        String username = "TestUser";

        //when
        String token = jwtService.generateAccessToken(username);

        //then

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(token).isNotBlank();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    void generateRefreshToken_ShouldGenerateValidRefreshToken() {
        //given
        String username = "UsernameTest";

        //when
        String refreshToken = jwtService.generateRefreshToken(username);

        //then
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        assertThat(claims.getSubject()).isNotBlank();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(new Date());

    }

    @Test
    void extractUsernameIfValid_ShouldExtractUsernameWhenValid(){
        //given
        String username = "UsernameTest";

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMs()))
                .signWith(key)
                .compact();


        //when
        String resultUsername = jwtService.extractUsernameIfValid(token);

        //then

        assertThat(resultUsername).isNotBlank();
        assertThat(resultUsername).isEqualTo(username);
    }

    @Test
    void extractUsernameIfValid_ShouldThrowWhenTokenInvalid(){
        //given
        String invalidToken = "invalid.token";
        //when/then
        assertThatThrownBy(() -> jwtService.extractUsernameIfValid(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid JWT token");
    }


}