package project.healthcare_appointment.service.auth_service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project.healthcare_appointment.dto.request.auth_request.*;
import project.healthcare_appointment.exception.*;
import project.healthcare_appointment.security.JwtUtil;
import project.healthcare_appointment.dto.response.auth_response.LoginResponse;
import project.healthcare_appointment.dto.response.auth_response.RefreshTokenResponse;
import project.healthcare_appointment.dto.response.auth_response.RegisterResponse;
import project.healthcare_appointment.enums.UserRole;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.model.UserProfile;
import project.healthcare_appointment.repository.UserProfileRepository;
import project.healthcare_appointment.repository.UserRepository;
import project.healthcare_appointment.service.InvalidatedTokenService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;

    UserProfileRepository userProfileRepository;

    PasswordEncoder passwordEncoder;

    InvalidatedTokenService invalidatedTokenService;

    JwtUtil jwtUtil;

    // Login
    @Override
    public LoginResponse login(LoginRequest request) {
       try{
           User user = userRepository.findUserWithoutRelationships(request.getUsername())
                   .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

           if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
               throw new AppException(ErrorCode.INVALID_CREDENTIALS);
           }

           if (!user.getIsActive()) {
               throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
           }
           if (!user.getIsEmailVerified()) {
               throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
           }

           String accessToken = jwtUtil.generateToken(user);
           String refreshToken = jwtUtil.generateRefreshToken(user);

           return LoginResponse.builder()
                   .accessToken(accessToken)
                   .refreshToken(refreshToken)
                   .userId(user.getId())
                   .email(user.getEmail())
                   .username(user.getUsername())
                   .role(user.getRole().name())
//                   .firstName(user.getUserProfile() != null ? user.getUserProfile().getFirstName() : "")
//                   .lastName(user.getUserProfile() != null ? user.getUserProfile().getLastName() : "")
                   .build();
       }catch  (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during login for user: {}", request.getUsername(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Login failed", e);
        }
    }

    // Register
    @Override
    public RegisterResponse register(RegisterRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new AppException(ErrorCode.USERNAME_TAKEN);
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_TAKEN);
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .isActive(true)
                    .isEmailVerified(false)
                    .build();

            User savedUser = userRepository.save(user);

            UserProfile profile = UserProfile.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(request.getPhone())
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(request.getGender())
                    .address(request.getAddress())
                    .user(savedUser)
                    .build();

            userProfileRepository.save(profile);

            log.info("User {} registered successfully", savedUser.getUsername());
            return new RegisterResponse("User registered successfully", savedUser.getId());
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during registration for user: {}", request.getUsername());
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Registration failed", e);
        }
    }

    // Refresh Token
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            if (!jwtUtil.verifyToken(request.getRefreshToken())) {
                throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
            }

            UUID userId = jwtUtil.getUserIdFromToken(request.getRefreshToken());
            // Find user to generate new tokens
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            jwtUtil.blacklistToken(request.getRefreshToken(), userId, "REFRESH" ,ipAddress, userAgent);

            String newAccessToken = jwtUtil.generateToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            return RefreshTokenResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing token");
            throw new AppException(ErrorCode.TOKEN_REFRESH_ERROR, e);
        }
    }

    @Transactional
    @Override
    public void logout(Authentication authentication, HttpServletRequest httpRequest) {
        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String token = jwt.getTokenValue();
            UUID userId = jwtUtil.getUserIdFromJwt(jwt);
            String ipAddress = getClientIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            jwtUtil.blacklistToken(token, userId, "LOGOUT", ipAddress, userAgent);
            log.info("User {} logged out successfully", userId);
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Logout failed", e);
        }
    }

    @Override
    @Transactional
    public void logoutAllDevices(Authentication authentication, HttpServletRequest httpRequest) {
        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String ipAddress = getClientIp(httpRequest);
            UUID userId = jwtUtil.getUserIdFromJwt(jwt);
            invalidatedTokenService.invalidateAllUserTokens(userId, "LOGOUT_ALL_DEVICES");
            log.info("All devices logged out for user {} from IP: {}", userId, ipAddress);
        } catch (Exception e) {
            log.error("Error during logout all devices: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Logout all devices failed", e);
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, HttpServletRequest httpRequest, Authentication authentication) {
        try{
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String currentToken = jwt.getTokenValue();
            UUID userId = jwtUtil.getUserIdFromJwt(jwt);
            String username = jwtUtil.getUsernameFromJwt(jwt);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Current password is incorrect");
            }

            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            String ipAddress = getClientIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            jwtUtil.blacklistToken(currentToken, userId, "PASSWORD_CHANGE", ipAddress, userAgent);



            log.info("Password changed successfully for user: {}", username);
        }catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Password change failed");
        }
    }

//    @Transactional
//    public void forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
//        try {
//            User user = userRepository.findByEmail(request.getEmail())
//                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
//            userRepository.save(user);
//
//            invalidatedTokenService.invalidateAllUserTokens(user.getId(), "PASSWORD_RESET");
//
//            String ipAddress = getClientIp(httpRequest);
//            log.info("Password reset successfully for user with email: {} from IP: {}",
//                    request.getEmail(), ipAddress);
//        } catch (AppException e) {
//            throw e;
//        } catch (Exception e) {
//            log.error("Error resetting password: {}", e.getMessage(), e);
//            throw new AppException(ErrorCode.INTERNAL_ERROR, "Password reset failed", e);
//        }
//    }

    // Verify Token
    @Override
    public boolean verifyToken(String token) {
        return jwtUtil.verifyToken(token);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

}
