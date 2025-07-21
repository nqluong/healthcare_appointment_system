package project.healthcare_appointment.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.healthcare_appointment.dto.request.auth_request.LoginRequest;
import project.healthcare_appointment.dto.request.auth_request.RegisterRequest;
import project.healthcare_appointment.dto.response.auth_response.LoginResponse;
import project.healthcare_appointment.dto.response.auth_response.RegisterResponse;
import project.healthcare_appointment.enums.Gender;
import project.healthcare_appointment.enums.UserRole;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.model.UserProfile;
import project.healthcare_appointment.repository.UserProfileRepository;
import project.healthcare_appointment.repository.UserRepository;
import project.healthcare_appointment.security.JwtUtil;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private InvalidatedTokenService invalidatedTokenService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserProfile testUserProfile;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(UserRole.PATIENT)
                .isActive(true)
                .isEmailVerified(true)
                .build();

        // Setup test user profile
        testUserProfile = UserProfile.builder()
                .firstName("Test")
                .lastName("User")
                .phone("1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .address("123 Test St")
                .user(testUser)
                .build();

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");
        registerRequest.setPhone("0987654321");
        registerRequest.setDateOfBirth(LocalDate.of(1995, 1, 1));
        registerRequest.setGender(Gender.FEMALE);
        registerRequest.setAddress("456 New St");
    }

    @Test
    void loginSuccess() {
        // Arrange
        when(userRepository.findUserWithoutRelationships("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getRole().name(), response.getRole());
    }

    @Test
    void loginWithInvalidCredentials() {
        // Arrange
        when(userRepository.findUserWithoutRelationships("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> authService.login(loginRequest));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    void loginWithInactiveAccount() {
        // Arrange
        testUser.setIsActive(false);
        when(userRepository.findUserWithoutRelationships("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> authService.login(loginRequest));
        assertEquals(ErrorCode.ACCOUNT_INACTIVE, exception.getErrorCode());
    }

    @Test
    void loginWithUnverifiedEmail() {
        // Arrange
        testUser.setIsEmailVerified(false);
        when(userRepository.findUserWithoutRelationships("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> authService.login(loginRequest));
        assertEquals(ErrorCode.EMAIL_NOT_VERIFIED, exception.getErrorCode());
    }

    @Test
    void registerSuccess() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testUserProfile);

        // Act
        RegisterResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUuid());
        verify(userRepository).save(any(User.class));
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void registerWithExistingUsername() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> authService.register(registerRequest));
        assertEquals(ErrorCode.USERNAME_TAKEN, exception.getErrorCode());
    }

    @Test
    void registerWithExistingEmail() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        AppException exception = assertThrows(AppException.class,
                () -> authService.register(registerRequest));
        assertEquals(ErrorCode.EMAIL_TAKEN, exception.getErrorCode());
    }
}
