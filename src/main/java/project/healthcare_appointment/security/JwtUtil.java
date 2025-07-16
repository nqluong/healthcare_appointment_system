package project.healthcare_appointment.security;

import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.healthcare_appointment.enums.TokenType;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.service.InvalidatedTokenService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static java.time.ZoneId.systemDefault;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtUtil {

    @Value("${jwt.signer-key}")
    String jwtSecret;

    @Value("${jwt.expiration}")
    Long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    Long refreshExpiration;

    @Autowired
    InvalidatedTokenService invalidatedTokenService;

    public String generateToken(User user) {
        try {
            return createToken(user, jwtExpiration);
        } catch (Exception e) {
            log.error("Error generating token for user: {}", user.getUsername(), e);
            throw new AppException(ErrorCode.TOKEN_GENERATION_ERROR, e);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            return createToken(user, refreshExpiration);
        } catch (Exception e) {
            log.error("Error generating refresh token for user: {}", user.getUsername(), e);
            throw new AppException(ErrorCode.TOKEN_GENERATION_ERROR, e);
        }
    }

    // Create Token with custom expiration
    private String createToken(User user, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(Instant.now().plus(expiration, ChronoUnit.SECONDS).toEpochMilli());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Verify Token
    public boolean verifyToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("Token is null or empty");
                return false;
            }
            if(invalidatedTokenService.isTokenInvalidated(token)) {
                log.warn("Token is blacklisted");
                return false;
            }
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getSubject() == null || claims.getSubject().isEmpty()) {
                log.warn("Token missing subject claim");
                return false;
            }

            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Get Username from Token
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }
    }

    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return UUID.fromString(claims.get("userId", String.class));
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting user ID from token", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("role", String.class);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting role from token", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }

    }
    public LocalDateTime getExpirationFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().toInstant()
                    .atZone(systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            log.error("Error extracting expiration from token", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new AppException(ErrorCode.TOKEN_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new AppException(ErrorCode.TOKEN_MALFORMED);
        } catch (SignatureException e) {
            throw new AppException(ErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.TOKEN_CLAIMS_EMPTY);
        }
    }

//    public String refreshToken(String refreshToken, String ipAddress, String userAgent) {
//        try {
//            if (verifyToken(refreshToken)) {
//                String username = getUsernameFromToken(refreshToken);
//                UUID userId = getUserIdFromToken(refreshToken);
//                String role = getRoleFromToken(refreshToken);
//                LocalDateTime oldTokenExpiration = getExpirationFromToken(refreshToken);
//
//                invalidatedTokenService.invalidateToken(
//                        refreshToken,
//                        userId,
//                        TokenType.REFRESH_TOKEN,
//                        oldTokenExpiration,
//                        "REFRESH",
//                        ipAddress,
//                        userAgent
//                );
//
//                Date now = new Date();
//                Date expiryDate = new Date(Instant.now().plus(jwtExpiration, ChronoUnit.SECONDS).toEpochMilli());
//
//                return Jwts.builder()
//                        .setSubject(username)
//                        .claim("userId", userId.toString())
//                        .claim("role", role)
//                        .setIssuedAt(now)
//                        .setExpiration(expiryDate)
//                        .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                        .compact();
//            }
//        } catch (AppException e) {
//            throw e;
//        } catch (Exception e) {
//            log.error("Error refreshing token", e);
//            throw new AppException(ErrorCode.TOKEN_REFRESH_ERROR, e);
//        }
//        throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
//    }

    public void blacklistToken(String token, UUID userId, String reason, String ipAddress, String userAgent) {
        try {
            LocalDateTime expiration = getExpirationFromToken(token);
            TokenType tokenType = token.length() > 200 ?
                    TokenType.REFRESH_TOKEN : TokenType.ACCESS_TOKEN;

            invalidatedTokenService.invalidateToken(token, userId, tokenType, expiration, reason, ipAddress, userAgent);
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage(), e);
        }
    }
}
