package project.healthcare_appointment.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import project.healthcare_appointment.enums.TokenType;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.service.InvalidatedTokenService;

import java.text.ParseException;
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

    private String createToken(User user, Long expiration) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("healthcare-appointment")
                    .issueTime(new Date())
                    .expirationTime(new Date(
                            Instant.now().plus(expiration, ChronoUnit.SECONDS).toEpochMilli()
                    ))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("userId", user.getId().toString())
                    .claim("role", user.getRole().name().toUpperCase())
                    .claim("email", user.getEmail())
                    .claim("isActive", user.getIsActive())
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(jwtSecret.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public boolean verifyToken(String token) {
        try {
            verifyTokenInternal(token, false);
            return true;
        } catch (Exception e) {
            log.debug("Token verification failed: {}", e.getMessage());
            return false;
        }
    }

    private SignedJWT verifyTokenInternal(String token, boolean isRefresh) throws JOSEException, ParseException {

        if (invalidatedTokenService.isTokenInvalidated(token)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        String userIdStr = signedJWT.getJWTClaimsSet().getStringClaim("userId");
        if (userIdStr != null) {
            UUID userId = UUID.fromString(userIdStr);
            if (invalidatedTokenService.isAllUserTokensInvalidated(userId)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        }

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(refreshExpiration, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            log.error("Error parsing token to get username", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }
    }

    public UUID getUserIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String userIdStr = signedJWT.getJWTClaimsSet().getStringClaim("userId");
            return UUID.fromString(userIdStr);
        } catch (ParseException e) {
            log.error("Error parsing token to get user ID", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getStringClaim("role");
        } catch (ParseException e) {
            log.error("Error parsing token to get role", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }
    }

    public Date getExpiresAtFromJwt(String token) {
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getExpirationTime();
        }catch (ParseException e){
            log.error("Error parsing token to get expiration time", e);
            throw new AppException(ErrorCode.TOKEN_PARSE_ERROR, e);
        }
    }

    public String getUsernameFromJwt(Jwt jwt) {
        return jwt.getSubject();
    }

    public UUID getUserIdFromJwt(Jwt jwt) {
        String userIdStr = jwt.getClaimAsString("userId");
        return UUID.fromString(userIdStr);
    }

    public String getRoleFromJwt(Jwt jwt) {
        return jwt.getClaimAsString("role");
    }

    public String getEmailFromJwt(Jwt jwt) {
        return jwt.getClaimAsString("email");
    }

    public Boolean getIsActiveFromJwt(Jwt jwt) {
        return jwt.getClaimAsBoolean("isActive");
    }


    public void blacklistToken(String token, UUID userId, String reason, String ipAddress, String userAgent) {
        try {
            invalidatedTokenService.invalidateToken(token, userId,reason, ipAddress, userAgent);
            log.info("Token blacklisted for user: {} with reason: {}", userId, reason);
        } catch (Exception e) {
            log.error("Error blacklisting token for user: {}", userId, e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to blacklist token", e);
        }
    }
}
