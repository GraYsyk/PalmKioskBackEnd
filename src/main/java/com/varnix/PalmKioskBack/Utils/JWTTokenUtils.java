package com.varnix.PalmKioskBack.Utils;

import com.varnix.PalmKioskBack.Entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JWTTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refreshlifetime}")
    private Duration refreshLifetime;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    public String generateToken(UserDetails userDetails) {
        return generateAccesToken(userDetails, lifetime);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateAccesToken(userDetails, refreshLifetime);
    }

    private String generateAccesToken(UserDetails userDetails, Duration tokenLifetime) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roleList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("roles", roleList);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + tokenLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        return getClaimsFromToken(token).get("roles", List.class);
    }
}
