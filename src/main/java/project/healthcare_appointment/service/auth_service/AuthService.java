package project.healthcare_appointment.service.auth_service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import project.healthcare_appointment.dto.request.auth_request.ChangePasswordRequest;
import project.healthcare_appointment.dto.request.auth_request.LoginRequest;
import project.healthcare_appointment.dto.request.auth_request.RefreshTokenRequest;
import project.healthcare_appointment.dto.request.auth_request.RegisterRequest;
import project.healthcare_appointment.dto.response.auth_response.LoginResponse;
import project.healthcare_appointment.dto.response.auth_response.RefreshTokenResponse;
import project.healthcare_appointment.dto.response.auth_response.RegisterResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest) ;

    RegisterResponse register(RegisterRequest registerRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshToken, HttpServletRequest request);

    void logout(Authentication authentication, HttpServletRequest request);

    void logoutAllDevices(Authentication authentication, HttpServletRequest request);

    void changePassword(ChangePasswordRequest changePasswordRequest, HttpServletRequest request, Authentication authentication);

    /**
     * Validates the JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean verifyToken(String token);
}
