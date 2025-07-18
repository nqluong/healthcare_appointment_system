package project.healthcare_appointment.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import project.healthcare_appointment.enums.TokenType;
import project.healthcare_appointment.model.InvalidatedToken;
import project.healthcare_appointment.repository.InvalidatedTokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidatedTokenService {

    InvalidatedTokenRepository invalidatedTokenRepository;

    public void invalidateToken(String token, UUID userId, String reason, String ipAddress, String userAgent) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            LocalDateTime expiresAt = claims.getExpirationTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            TokenType tokenType = determineTokenType(reason, claims);

            invalidateToken(token, userId, tokenType, expiresAt, reason, ipAddress, userAgent);
        } catch (ParseException e) {
            log.error("Error parsing token for invalidation: {}", e.getMessage());
            LocalDateTime defaultExpiration = LocalDateTime.now().plusHours(1);
            invalidateToken(token, userId, TokenType.ACCESS_TOKEN, defaultExpiration, reason, ipAddress, userAgent);
        }
    }
    public void invalidateToken(String token, UUID userId, TokenType tokenType,
                                LocalDateTime expiresAt, String reason, String ipAddress, String userAgent) {
        try {
            String tokenHash = hashToken(token);

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .tokenHash(tokenHash)
                    .userId(userId)
                    .tokenType(tokenType)
                    .expiresAt(expiresAt)
                    .reason(reason)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
            log.info("Token blacklisted for user {} with reason: {}", userId, reason);
        } catch (Exception e) {
            log.error("Error blacklisting token for user {}: {}", userId, e.getMessage());
        }
    }

    public boolean isTokenInvalidated(String token) {
        try {
            String tokenHash = hashToken(token);
            return invalidatedTokenRepository.existsByTokenHash(tokenHash);
        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public void invalidateAllUserTokens(UUID userId, String reason) {
        try {
            InvalidatedToken invalidateAllToken = InvalidatedToken.builder()
                    .tokenHash("ALL_TOKENS_" + userId.toString())
                    .userId(userId)
                    .tokenType(TokenType.ACCESS_TOKEN)
                    .expiresAt(LocalDateTime.now().plusDays(30))
                    .reason(reason)
                    .ipAddress("SYSTEM")
                    .userAgent("SYSTEM")
                    .build();

            invalidatedTokenRepository.save(invalidateAllToken);
            log.info("All tokens invalidated for user {} with reason: {}", userId, reason);
        } catch (Exception e) {
            log.error("Error invalidating all tokens for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to invalidate all user tokens", e);
        }
    }

    public boolean isAllUserTokensInvalidated(UUID userId) {
        try {
            String allTokensHash = "ALL_TOKENS_" + userId.toString();
            return invalidatedTokenRepository.existsByTokenHash(allTokensHash);
        } catch (Exception e) {
            log.error("Error checking if all user tokens are invalidated: {}", e.getMessage());
            return false;
        }
    }

    public List<InvalidatedToken> getUserBlacklistedTokens(UUID userId) {
        return invalidatedTokenRepository.findByUserId(userId);
    }

    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            invalidatedTokenRepository.deleteExpiredTokens(now);
            log.info("Expired blacklisted tokens cleaned up at {}", now);
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens: {}", e.getMessage());
        }
    }

    private TokenType determineTokenType(String reason, JWTClaimsSet claims) {
        if (reason.contains("REFRESH")) {
            return TokenType.REFRESH_TOKEN;
        }

        long expirationTime = claims.getExpirationTime().getTime();
        long issueTime = claims.getIssueTime().getTime();
        long tokenLifetime = expirationTime - issueTime;

        if (tokenLifetime > 3600000) {
            return TokenType.REFRESH_TOKEN;
        }

        return TokenType.ACCESS_TOKEN;
    }

    public long getActiveBlacklistedTokensCount() {
        return invalidatedTokenRepository.countActiveBlacklistedTokens(LocalDateTime.now());
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
