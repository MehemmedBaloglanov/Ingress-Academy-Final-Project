package com.lastversion.user.security_and_jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final String SECRET_KEY = "secret";

    // Username çıxarılması
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Rolları çıxarırıq
    public Set<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return Set.of(claims.get("roles").toString().split(","));
    }

    // Tokenin bitmə vaxtı çıxarılması
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Rollarla birlikdə token yaradılması
    public String generateToken(String username, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", String.join(",", roles));  // Rolları token-ə əlavə edirik
        return createToken(claims, username);
    }

    // Tokenin yaradılması
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 saatlıq keçərlilik
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Tokenin doğrulanması (username və tokenin vaxtına əsasən)
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
