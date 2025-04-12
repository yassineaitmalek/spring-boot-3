package com.yatmk.infrastructure.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.yatmk.infrastructure.security.service.UserContainer;
import com.yatmk.persistence.exception.config.ClientSideException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Key key;

    private static final Long VALIDITY = 1000l * 60l * 120l;

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractToken(String authHeader) {
        return Optional.ofNullable(authHeader)
                .filter(e -> e.startsWith("Bearer "))
                .map(e -> e.substring(7))
                .orElseThrow(() -> new ClientSideException("authHeader is not valid"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserContainer userDetails) {

        return Optional.ofNullable(token)
                .filter(Predicate.not(this::isTokenExpired))
                .map(this::extractUserId)
                .map(e -> e.equals(userDetails.getId()))
                .orElseGet(() -> Boolean.FALSE);

    }

    public String generateToken(String userId) {

        return createToken(new HashMap<>(), userId);
    }

    private String createToken(Map<String, Object> claims, String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId, Long now, Long expirationInMillis) {

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationInMillis))
                .claim("scope", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
