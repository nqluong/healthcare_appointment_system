package project.healthcare_appointment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import project.healthcare_appointment.dto.request.auth_request.*;
import project.healthcare_appointment.dto.response.*;
import project.healthcare_appointment.dto.response.auth_response.LoginResponse;
import project.healthcare_appointment.dto.response.auth_response.LogoutResponse;
import project.healthcare_appointment.dto.response.auth_response.RefreshTokenResponse;
import project.healthcare_appointment.dto.response.auth_response.RegisterResponse;
import project.healthcare_appointment.service.auth_service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class)))
    })
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request,
                                                             HttpServletRequest httpRequest) {
        RefreshTokenResponse response = authService.refreshToken(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and blacklist current token",
        security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<LogoutResponse> logout(Authentication authentication,
                                                 HttpServletRequest httpRequest) {
        try {
            authService.logout(authentication, httpRequest);
            return ResponseEntity.ok(LogoutResponse.builder()
                    .message("Logged out successfully")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LogoutResponse.builder()
                            .message("Logout failed")
                            .success(false)
                            .build());
        }
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout all devices", description = "Logout user from all devices",
            security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<LogoutResponse> logoutAllDevices(Authentication authentication,
                                                           HttpServletRequest httpRequest) {
        try {
            authService.logoutAllDevices(authentication, httpRequest);
            return ResponseEntity.ok(LogoutResponse.builder()
                    .message("Logged out from all devices successfully")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LogoutResponse.builder()
                            .message("Logout all devices failed")
                            .success(false)
                            .build());
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify JWT token", description = "Verify if JWT token is valid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token verification result",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Boolean>> verifyToken(@RequestBody VerifyTokenRequest request) {
        boolean isValid = authService.verifyToken(request.getToken());
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user's password with current password verification",
        security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid current password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        authService.changePassword(request, httpRequest ,authentication);
        return ResponseEntity.ok("Password changed successfully");
    }

//    @PostMapping("/forgot-password")
//    @Operation(summary = "Forgot password", description = "Reset password using email")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
//                                                 HttpServletRequest httpRequest) {
//        authService.forgotPassword(request, httpRequest);
//        return ResponseEntity.ok("Password reset successfully");
//    }

}
