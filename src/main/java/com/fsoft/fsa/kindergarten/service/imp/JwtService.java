package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.model.auth.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(AuthUser authUser) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", authUser.getUser().getId());
        hashMap.put("role", authUser.getUser().getRole().getName());
        hashMap.put("status", authUser.getUser().getStatus());
        hashMap.put("fullName", authUser.getUser().getFullName());
        hashMap.put("isDeleted", authUser.getUser().isDeleted());
        return generateToken(hashMap, authUser);
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (ACCESS_TOKEN_EXPIRATION)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isExpiredToken(token);
    }

    private boolean isExpiredToken(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private Integer extractId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("id", Integer.class);
    }

    private String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    public boolean isActiveUser(String token) {
        Claims claims = extractAllClaims(token);
        String status = claims.get("status", String.class);
        return status.equalsIgnoreCase("Active");
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Integer getIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null || authentication.isAuthenticated()) {
            String token = (String) authentication.getCredentials();
            return extractId(token);
        }
        return null;
    }

    public String getRoleFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null || authentication.isAuthenticated()) {
            String token = (String) authentication.getCredentials();
            return extractRole(token);
        }
        return null;
    }

    public boolean isDeletedUser(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("isDeleted", Boolean.class);
    }


}
