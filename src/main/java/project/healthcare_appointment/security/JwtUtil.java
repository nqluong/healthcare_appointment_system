package project.healthcare_appointment.security;

import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.model.User;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtUtil {

    @Value("${jwt.signer-key}")
    String jwtSecret;

    @Value("${jwt.expiration}")
    Long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    Long refreshExpiration;

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
        Date expiryDate = new Date(now.getTime() + expiration);

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

    public String refreshToken(String refreshToken) {
        try {
            if (verifyToken(refreshToken)) {
                String username = getUsernameFromToken(refreshToken);
                UUID userId = getUserIdFromToken(refreshToken);
                String role = getRoleFromToken(refreshToken);

                Date now = new Date();
                Date expiryDate = new Date(now.getTime() + jwtExpiration);

                return Jwts.builder()
                        .setSubject(username)
                        .claim("userId", userId.toString())
                        .claim("role", role)
                        .setIssuedAt(now)
                        .setExpiration(expiryDate)
                        .signWith(SignatureAlgorithm.HS512, jwtSecret)
                        .compact();
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new AppException(ErrorCode.TOKEN_REFRESH_ERROR, e);
        }
        throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
    }
}
