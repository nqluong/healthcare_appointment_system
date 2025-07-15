package project.healthcare_appointment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.healthcare_appointment.exception.*;
import project.healthcare_appointment.security.JwtUtil;
import project.healthcare_appointment.dto.request.LoginRequest;
import project.healthcare_appointment.dto.request.RefreshTokenRequest;
import project.healthcare_appointment.dto.request.RegisterRequest;
import project.healthcare_appointment.dto.response.LoginResponse;
import project.healthcare_appointment.dto.response.RefreshTokenResponse;
import project.healthcare_appointment.dto.response.RegisterResponse;
import project.healthcare_appointment.enums.UserRole;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.model.UserProfile;
import project.healthcare_appointment.repository.UserProfileRepository;
import project.healthcare_appointment.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;

    UserProfileRepository userProfileRepository;

    PasswordEncoder passwordEncoder;

    JwtUtil jwtUtil;

    // Login
    public LoginResponse login(LoginRequest request) {
       try{
           User user = userRepository.findByUsername(request.getUsername())
                   .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                           "User not found with username: " + request.getUsername()));

           if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
               throw AppException.builder(ErrorCode.INVALID_CREDENTIALS)
                       .property("username", request.getUsername())
                       .build();
           }

           if (!user.getIsActive()) {
               throw AppException.builder(ErrorCode.ACCOUNT_INACTIVE)
                       .property("username", request.getUsername())
                       .build();
           }
           if (!user.getIsEmailVerified()) {
               throw AppException.builder(ErrorCode.EMAIL_NOT_VERIFIED)
                       .property("email", user.getEmail())
                       .build();
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
                   .firstName(user.getUserProfile() != null ? user.getUserProfile().getFirstName() : "")
                   .lastName(user.getUserProfile() != null ? user.getUserProfile().getLastName() : "")
                   .build();
       }catch  (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during login for user: {}", request.getUsername(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Login failed", e);
        }
    }

    // Register
    public RegisterResponse register(RegisterRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw AppException.builder(ErrorCode.USERNAME_TAKEN)
                        .property("field", "username")
                        .property("value", request.getUsername())
                        .build();
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                throw AppException.builder(ErrorCode.EMAIL_TAKEN)
                        .property("field", "email")
                        .property("value", request.getEmail())
                        .build();
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(UserRole.PATIENT)
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
            log.error("Error during registration for user: {}", request.getUsername(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Registration failed", e);
        }
    }

    // Refresh Token
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        try{
            String newAccessToken = jwtUtil.refreshToken(request.getRefreshToken());
            return RefreshTokenResponse.builder()
                    .accessToken(newAccessToken)
                    .build();
        }catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new AppException(ErrorCode.TOKEN_REFRESH_ERROR, e);
        }
    }

    // Verify Token
    public boolean verifyToken(String token) {
        return jwtUtil.verifyToken(token);
    }
}
