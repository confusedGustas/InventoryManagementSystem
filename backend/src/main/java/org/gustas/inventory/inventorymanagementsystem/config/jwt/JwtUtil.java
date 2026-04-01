package org.gustas.inventory.inventorymanagementsystem.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public static final String ROLE_CLAIM = "role";
    public static final String FIRST_NAME_CLAIM = "firstName";
    public static final String LAST_NAME_CLAIM = "lastName";

    @Value("${spring.security.jwt.secret}")
    private String SECRET;

    public String generateToken(String username, String role, String firstName, String lastName) {
        return Jwts.builder()
                .setSubject(username)
                .claim(ROLE_CLAIM, role)
                .claim(FIRST_NAME_CLAIM, firstName)
                .claim(LAST_NAME_CLAIM, lastName)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get(ROLE_CLAIM, String.class);
    }

}
