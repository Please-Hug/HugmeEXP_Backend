package org.example.hugmeexp.global.infra.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.example.hugmeexp.domain.user.enums.UserRole;
import org.example.hugmeexp.global.common.exception.ErrorCode;
import org.example.hugmeexp.global.infra.auth.exception.InvalidAccessTokenException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String USED_TOKEN_PREFIX = "used_refresh_token:";

    private final String secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    private SecretKey key;

    public JwtTokenProvider(@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                            @Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.redisTemplate = redisTemplate;
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public AuthResponse createToken(String email) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpiration);
        String accessToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key)
                .compact();

        Date refreshTokenExpiresIn = new Date(now + refreshTokenExpiration);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key)
                .compact();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public void setRefreshTokenAsUsed(String token) {
        long expiration = getClaims(token).getExpiration().getTime() - System.currentTimeMillis();
        redisTemplate.opsForValue().set(USED_TOKEN_PREFIX + token, "used", expiration, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public boolean isRefreshTokenUsed(String token) {
        Boolean hasKey = redisTemplate.hasKey(USED_TOKEN_PREFIX + token);
        return hasKey != null && hasKey;
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public long getTokenRemainingTimeMillis(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            return Math.max(0, expiration.getTime() - System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("Failed to extract expiration from token: {}", e.getMessage(), e);
            return 0;
        }
    }

    public String createAccessToken(String username, String role) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpiration);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String username, UserRole role) {
        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + refreshTokenExpiration);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", "ROLE_" + role.name())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key)
                .compact();
    }

    public void revokeRefreshToken(String token) {
        setRefreshTokenAsUsed(token);
    }

    public boolean isRefreshTokenRevoked(String refreshToken) {
        return isRefreshTokenUsed(refreshToken);
    }

    public void validateAccessTokenForReissue(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            // If parsing succeeds, the token is still valid.
            throw new InvalidAccessTokenException(ErrorCode.ACCESS_TOKEN_STILL_VALID);
        } catch (ExpiredJwtException e) {
            // This is the expected case for reissue, so we do nothing.
        } catch (Exception e) {
            // Any other exception means the token is invalid for other reasons.
            throw new InvalidAccessTokenException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }
}
