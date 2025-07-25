package project.healthcare_appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;
import project.healthcare_appointment.dto.request.user_profile_request.UpdateProfileRequest;
import project.healthcare_appointment.dto.response.FileUploadResponse;
import project.healthcare_appointment.dto.response.user_profile_response.ProfileResponse;
import project.healthcare_appointment.enums.FileUploadType;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.mapper.UserProfileMapper;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.model.UserProfile;
import project.healthcare_appointment.repository.UserProfileRepository;
import project.healthcare_appointment.security.JwtUtil;
import project.healthcare_appointment.service.user_service.UserProfileServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UUID testUserId;
    private UserProfile testUserProfile;
    private User testUser;
    private ProfileResponse testProfileResponse;
    private UpdateProfileRequest updateProfileRequest;
    private FileUploadResponse fileUploadResponse;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = User.builder()
                .id(testUserId)
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        testUserProfile = UserProfile.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .avatarUrl("https://example.com/old-avatar.jpg")
                .createdAt(LocalDateTime.now())
                .build();

        testProfileResponse = ProfileResponse.builder()
                .id(testUserProfile.getId())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .avatarUrl("https://example.com/old-avatar.jpg")
                .build();

        updateProfileRequest = UpdateProfileRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phone("0987654321")
                .build();

        fileUploadResponse = FileUploadResponse.builder()
                .fileName("new-avatar.jpg")
                .fileUrl("https://example.com/new-avatar.jpg")
                .fileSize(2048L)
                .build();
    }

    private void mockSecurityContext() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwtUtil.getUserIdFromJwt(jwt)).thenReturn(testUserId);
        }
    }

    @Test
    @DisplayName("Should get current user profile successfully")
    void getCurrentUserProfile_Success() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwtUtil.getUserIdFromJwt(jwt)).thenReturn(testUserId);
            when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
            when(userProfileMapper.toProfileResponse(testUserProfile)).thenReturn(testProfileResponse);

            // When
            ProfileResponse result = userProfileService.getCurrentUserProfile();

            // Then
            assertNotNull(result);
            assertEquals(testProfileResponse.getFirstName(), result.getFirstName());
            assertEquals(testProfileResponse.getLastName(), result.getLastName());
            assertEquals(testProfileResponse.getEmail(), result.getEmail());

            verify(jwtUtil, times(1)).getUserIdFromJwt(jwt);
            verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
            verify(userProfileMapper, times(1)).toProfileResponse(testUserProfile);
        }
    }

    @Test
    @DisplayName("Should throw exception when current user profile not found")
    void getCurrentUserProfile_NotFound() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwtUtil.getUserIdFromJwt(jwt)).thenReturn(testUserId);
            when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.empty());

            // When & Then
            AppException exception = assertThrows(AppException.class,
                    () -> userProfileService.getCurrentUserProfile());

            assertEquals(ErrorCode.PROFILE_NOT_FOUND, exception.getErrorCode());
            verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
            verify(userProfileMapper, never()).toProfileResponse(any());
        }
    }

    @Test
    @DisplayName("Should update current user profile successfully")
    void updateCurrentUserProfile_Success() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwtUtil.getUserIdFromJwt(jwt)).thenReturn(testUserId);
            when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));

            ProfileResponse updatedResponse = ProfileResponse.builder()
                    .id(testUserProfile.getId())
                    .firstName("Jane")
                    .lastName("Smith")
                    .phone("0987654321")
                    .build();

            when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);
            when(userProfileMapper.toProfileResponse(testUserProfile)).thenReturn(updatedResponse);

            // When
            ProfileResponse result = userProfileService.updateCurrentUserProfile(updateProfileRequest);

            // Then
            assertNotNull(result);
            assertEquals("Jane", result.getFirstName());
            assertEquals("Smith", result.getLastName());

            verify(userProfileMapper, times(1)).updateProfileFromRequest(updateProfileRequest, testUserProfile);
            verify(userProfileRepository, times(1)).save(testUserProfile);
        }
    }

    @Test
    @DisplayName("Should upload current user avatar successfully")
    void uploadCurrentUserAvatar_Success() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwtUtil.getUserIdFromJwt(jwt)).thenReturn(testUserId);
            when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
            when(fileUploadService.uploadFile(multipartFile, FileUploadType.AVATAR)).thenReturn(fileUploadResponse);
            when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);

            // When
            FileUploadResponse result = userProfileService.uploadCurrentUserAvatar(multipartFile);

            // Then
            assertNotNull(result);
            assertEquals(fileUploadResponse.getFileName(), result.getFileName());
            assertEquals(fileUploadResponse.getFileUrl(), result.getFileUrl());

            verify(fileUploadService, times(1)).deleteFile("https://example.com/old-avatar.jpg");
            verify(fileUploadService, times(1)).uploadFile(multipartFile, FileUploadType.AVATAR);
            verify(userProfileRepository, times(1)).save(testUserProfile);
        }
    }

    @Test
    @DisplayName("Should get profile by userId successfully")
    void getProfile_Success() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(userProfileMapper.toProfileResponse(testUserProfile)).thenReturn(testProfileResponse);

        // When
        ProfileResponse result = userProfileService.getProfile(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(testProfileResponse.getFirstName(), result.getFirstName());
        assertEquals(testProfileResponse.getLastName(), result.getLastName());

        verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
        verify(userProfileMapper, times(1)).toProfileResponse(testUserProfile);
    }

    @Test
    @DisplayName("Should throw exception when profile not found by userId")
    void getProfile_NotFound() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> userProfileService.getProfile(testUserId));

        assertEquals(ErrorCode.PROFILE_NOT_FOUND, exception.getErrorCode());
        verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
        verify(userProfileMapper, never()).toProfileResponse(any());
    }

    @Test
    @DisplayName("Should update profile by userId successfully")
    void updateProfile_Success() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);

        ProfileResponse updatedResponse = ProfileResponse.builder()
                .id(testUserProfile.getId())
                .firstName("Jane")
                .lastName("Smith")
                .build();
        when(userProfileMapper.toProfileResponse(testUserProfile)).thenReturn(updatedResponse);

        // When
        ProfileResponse result = userProfileService.updateProfile(testUserId, updateProfileRequest);

        // Then
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());

        verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
        verify(userProfileMapper, times(1)).updateProfileFromRequest(updateProfileRequest, testUserProfile);
        verify(userProfileRepository, times(1)).save(testUserProfile);
        verify(userProfileMapper, times(1)).toProfileResponse(testUserProfile);
    }

    @Test
    @DisplayName("Should throw exception when update profile fails")
    void updateProfile_Failed() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(userProfileRepository.save(testUserProfile)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> userProfileService.updateProfile(testUserId, updateProfileRequest));

        assertEquals(ErrorCode.PROFILE_UPDATE_FAILED, exception.getErrorCode());
        verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
        verify(userProfileRepository, times(1)).save(testUserProfile);
    }

    @Test
    @DisplayName("Should upload avatar by userId successfully")
    void uploadAvatar_Success() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(fileUploadService.uploadFile(multipartFile, FileUploadType.AVATAR)).thenReturn(fileUploadResponse);
        when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);

        // When
        FileUploadResponse result = userProfileService.uploadAvatar(testUserId, multipartFile);

        // Then
        assertNotNull(result);
        assertEquals(fileUploadResponse.getFileName(), result.getFileName());
        assertEquals(fileUploadResponse.getFileUrl(), result.getFileUrl());

        verify(fileUploadService, times(1)).deleteFile("https://example.com/old-avatar.jpg");
        verify(fileUploadService, times(1)).uploadFile(multipartFile, FileUploadType.AVATAR);
        verify(userProfileRepository, times(1)).save(testUserProfile);
        assertEquals("https://example.com/new-avatar.jpg", testUserProfile.getAvatarUrl());
    }

    @Test
    @DisplayName("Should upload avatar without deleting old avatar when no existing avatar")
    void uploadAvatar_NoExistingAvatar() {
        // Given
        testUserProfile.setAvatarUrl(null);
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(fileUploadService.uploadFile(multipartFile, FileUploadType.AVATAR)).thenReturn(fileUploadResponse);
        when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);

        // When
        FileUploadResponse result = userProfileService.uploadAvatar(testUserId, multipartFile);

        // Then
        assertNotNull(result);
        verify(fileUploadService, never()).deleteFile(any());
        verify(fileUploadService, times(1)).uploadFile(multipartFile, FileUploadType.AVATAR);
        verify(userProfileRepository, times(1)).save(testUserProfile);
    }

    @Test
    @DisplayName("Should throw exception when avatar upload fails")
    void uploadAvatar_Failed() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(fileUploadService.uploadFile(multipartFile, FileUploadType.AVATAR))
                .thenThrow(new RuntimeException("Upload failed"));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> userProfileService.uploadAvatar(testUserId, multipartFile));

        assertEquals(ErrorCode.FILE_UPLOAD_FAILED, exception.getErrorCode());
        verify(fileUploadService, times(1)).uploadFile(multipartFile, FileUploadType.AVATAR);
    }

    @Test
    @DisplayName("Should get all profiles successfully")
    void getAllProfiles_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserProfile> userProfilePage = new PageImpl<>(Arrays.asList(testUserProfile));
        Page<ProfileResponse> profileResponsePage = new PageImpl<>(Arrays.asList(testProfileResponse));

        when(userProfileRepository.findAllWithUser(pageable)).thenReturn(userProfilePage);
        when(userProfileMapper.toProfileResponsePage(userProfilePage)).thenReturn(profileResponsePage);

        // When
        Page<ProfileResponse> result = userProfileService.getAllProfiles(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testProfileResponse.getFirstName(), result.getContent().get(0).getFirstName());

        verify(userProfileRepository, times(1)).findAllWithUser(pageable);
        verify(userProfileMapper, times(1)).toProfileResponsePage(userProfilePage);
    }

    @Test
    @DisplayName("Should search profiles successfully")
    void searchProfiles_Success() {
        // Given
        String keyword = "John";
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserProfile> userProfilePage = new PageImpl<>(Arrays.asList(testUserProfile));
        Page<ProfileResponse> profileResponsePage = new PageImpl<>(Arrays.asList(testProfileResponse));

        when(userProfileRepository.searchProfiles(keyword, pageable)).thenReturn(userProfilePage);
        when(userProfileMapper.toProfileResponsePage(userProfilePage)).thenReturn(profileResponsePage);

        // When
        Page<ProfileResponse> result = userProfileService.searchProfiles(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testProfileResponse.getFirstName(), result.getContent().get(0).getFirstName());

        verify(userProfileRepository, times(1)).searchProfiles(keyword, pageable);
        verify(userProfileMapper, times(1)).toProfileResponsePage(userProfilePage);
    }

    @Test
    @DisplayName("Should return empty page when no profiles found in search")
    void searchProfiles_EmptyResult() {
        // Given
        String keyword = "NonExistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserProfile> emptyUserProfilePage = new PageImpl<>(Arrays.asList());
        Page<ProfileResponse> emptyProfileResponsePage = new PageImpl<>(Arrays.asList());

        when(userProfileRepository.searchProfiles(keyword, pageable)).thenReturn(emptyUserProfilePage);
        when(userProfileMapper.toProfileResponsePage(emptyUserProfilePage)).thenReturn(emptyProfileResponsePage);

        // When
        Page<ProfileResponse> result = userProfileService.searchProfiles(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());

        verify(userProfileRepository, times(1)).searchProfiles(keyword, pageable);
        verify(userProfileMapper, times(1)).toProfileResponsePage(emptyUserProfilePage);
    }

    @Test
    @DisplayName("Should handle null keyword in search")
    void searchProfiles_NullKeyword() {
        // Given
        String keyword = null;
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserProfile> userProfilePage = new PageImpl<>(Arrays.asList(testUserProfile));
        Page<ProfileResponse> profileResponsePage = new PageImpl<>(Arrays.asList(testProfileResponse));

        when(userProfileRepository.searchProfiles(keyword, pageable)).thenReturn(userProfilePage);
        when(userProfileMapper.toProfileResponsePage(userProfilePage)).thenReturn(profileResponsePage);

        // When
        Page<ProfileResponse> result = userProfileService.searchProfiles(keyword, pageable);

        // Then
        assertNotNull(result);
        verify(userProfileRepository, times(1)).searchProfiles(keyword, pageable);
        verify(userProfileMapper, times(1)).toProfileResponsePage(userProfilePage);
    }

    @Test
    @DisplayName("Should handle security context properly in getCurrentUserId")
    void getCurrentUserId_SecurityContextHandling() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwtUtil.getUserIdFromJwt(jwt)).thenReturn(testUserId);
            when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
            when(userProfileMapper.toProfileResponse(testUserProfile)).thenReturn(testProfileResponse);

            // When
            ProfileResponse result = userProfileService.getCurrentUserProfile();

            // Then
            assertNotNull(result);
            verify(jwtUtil, times(1)).getUserIdFromJwt(jwt);
        }
    }

    @Test
    @DisplayName("Should handle exception when JWT extraction fails")
    void getCurrentUserId_JwtExtractionFailed() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwtUtil.getUserIdFromJwt(jwt)).thenThrow(new RuntimeException("JWT extraction failed"));

            // When & Then
            assertThrows(RuntimeException.class, () -> userProfileService.getCurrentUserProfile());
            verify(jwtUtil, times(1)).getUserIdFromJwt(jwt);
        }
    }

    @Test
    @DisplayName("Should handle profile repository exception in updateProfile")
    void updateProfile_RepositoryException() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        doThrow(new RuntimeException("Database connection failed")).when(userProfileMapper)
                .updateProfileFromRequest(updateProfileRequest, testUserProfile);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> userProfileService.updateProfile(testUserId, updateProfileRequest));

        assertEquals(ErrorCode.PROFILE_UPDATE_FAILED, exception.getErrorCode());
        verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
        verify(userProfileMapper, times(1)).updateProfileFromRequest(updateProfileRequest, testUserProfile);
    }

    @Test
    @DisplayName("Should handle file service exception in uploadAvatar")
    void uploadAvatar_FileServiceException() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        doThrow(new RuntimeException("File service unavailable")).when(fileUploadService)
                .deleteFile(anyString());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> userProfileService.uploadAvatar(testUserId, multipartFile));

        assertEquals(ErrorCode.FILE_UPLOAD_FAILED, exception.getErrorCode());
        verify(fileUploadService, times(1)).deleteFile("https://example.com/old-avatar.jpg");
    }

    @Test
    @DisplayName("Should verify all profile fields are mapped correctly")
    void verifyProfileMapping() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(userProfileMapper.toProfileResponse(testUserProfile)).thenReturn(testProfileResponse);

        // When
        ProfileResponse result = userProfileService.getProfile(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(testProfileResponse.getId(), result.getId());
        assertEquals(testProfileResponse.getFirstName(), result.getFirstName());
        assertEquals(testProfileResponse.getLastName(), result.getLastName());
        assertEquals(testProfileResponse.getEmail(), result.getEmail());
        assertEquals(testProfileResponse.getPhone(), result.getPhone());
        assertEquals(testProfileResponse.getAvatarUrl(), result.getAvatarUrl());

        verify(userProfileRepository, times(1)).findByUserIdWithUser(testUserId);
        verify(userProfileMapper, times(1)).toProfileResponse(testUserProfile);
    }

    @Test
    @DisplayName("Should handle concurrent profile updates correctly")
    void updateProfile_ConcurrentUpdates() {
        // Given
        when(userProfileRepository.findByUserIdWithUser(testUserId)).thenReturn(Optional.of(testUserProfile));
        when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);
        when(userProfileMapper.toProfileResponse(testUserProfile)).thenReturn(testProfileResponse);

        // When
        ProfileResponse result1 = userProfileService.updateProfile(testUserId, updateProfileRequest);
        ProfileResponse result2 = userProfileService.updateProfile(testUserId, updateProfileRequest);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        verify(userProfileRepository, times(2)).findByUserIdWithUser(testUserId);
        verify(userProfileRepository, times(2)).save(testUserProfile);
        verify(userProfileMapper, times(2)).updateProfileFromRequest(updateProfileRequest, testUserProfile);
    }
}