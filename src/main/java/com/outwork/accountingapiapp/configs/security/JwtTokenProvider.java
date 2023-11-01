package com.outwork.accountingapiapp.configs.security;

import com.outwork.accountingapiapp.models.security.SecuredUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.expiredDuration}")
    private int jwtExpiredDuration;

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String PREFIX_BEARER = "Bearer ";

    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String createToken(Authentication authentication) {

        SecuredUserDetails userDetails = (SecuredUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiredDuration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }


    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(PREFIX_BEARER)) {
            return bearerToken.substring(PREFIX_BEARER.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


}
