package project.healthcare_appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import project.healthcare_appointment.TestSecurityConfig;
import project.healthcare_appointment.dto.request.user_profile_request.UpdateProfileRequest;
import project.healthcare_appointment.dto.response.FileUploadResponse;
import project.healthcare_appointment.dto.response.user_profile_response.ProfileResponse;
import project.healthcare_appointment.enums.Gender;
import project.healthcare_appointment.service.user_service.UserProfileService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
@DisplayName("UserProfileController Tests")
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProfileResponse sampleProfileResponse;
    private UpdateProfileRequest sampleUpdateRequest;
    private UUID sampleUserId;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();

        sampleProfileResponse = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .gender(Gender.MALE)
                .address("123 Main St, City, State")
                .avatarUrl("https://example.com/avatar.jpg")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("johndoe")
                .email("john.doe@example.com")
                .role("USER")
                .isActive(true)
                .build();

        sampleUpdateRequest = UpdateProfileRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .gender(Gender.MALE)
                .address("123 Main St, City, State")
                .build();
    }

    @Nested
    @DisplayName("Get Current User Profile Tests")
    class GetCurrentUserProfileTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully return current user profile")
        void getCurrentUserProfile_Success() throws Exception {
            // Given
            when(userProfileService.getCurrentUserProfile()).thenReturn(sampleProfileResponse);

            // When & Then
            mockMvc.perform(get("/api/profile/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                    .andExpect(jsonPath("$.username").value("johndoe"));

            verify(userProfileService, times(1)).getCurrentUserProfile();
        }

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void getCurrentUserProfile_Unauthorized() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/profile/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(userProfileService, never()).getCurrentUserProfile();
        }
    }

    @Nested
    @DisplayName("Get User Profile By ID Tests")
    class GetUserProfileByIdTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully return user profile by ID")
        void getUserProfile_Success() throws Exception {
            // Given
            when(userProfileService.getProfile(sampleUserId)).thenReturn(sampleProfileResponse);

            // When & Then
            mockMvc.perform(get("/api/profile/{userId}", sampleUserId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));

            verify(userProfileService, times(1)).getProfile(sampleUserId);
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle invalid UUID format")
        void getUserProfile_InvalidUUID() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/profile/invalid-uuid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userProfileService, never()).getProfile(any());
        }
    }

    @Nested
    @DisplayName("Update Current User Profile Tests")
    class UpdateCurrentUserProfileTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully update current user profile")
        void updateCurrentUserProfile_Success() throws Exception {
            // Given
            ProfileResponse updatedProfile = ProfileResponse.builder()
                    .id(sampleProfileResponse.getId())
                    .firstName("Jane")
                    .lastName("Smith")
                    .phone("+9876543210")
                    .dateOfBirth(LocalDate.of(1985, 5, 20))
                    .gender(Gender.FEMALE)
                    .address("456 Oak Ave, New City, State")
                    .avatarUrl(sampleProfileResponse.getAvatarUrl())
                    .createdAt(sampleProfileResponse.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .username(sampleProfileResponse.getUsername())
                    .email(sampleProfileResponse.getEmail())
                    .role(sampleProfileResponse.getRole())
                    .isActive(sampleProfileResponse.getIsActive())
                    .build();

            UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .phone("+9876543210")
                    .dateOfBirth(LocalDate.of(1985, 5, 20))
                    .gender(Gender.FEMALE)
                    .address("456 Oak Ave, New City, State")
                    .build();

            when(userProfileService.updateCurrentUserProfile(any(UpdateProfileRequest.class)))
                    .thenReturn(updatedProfile);

            // When & Then
            mockMvc.perform(put("/api/profile/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Jane"))
                    .andExpect(jsonPath("$.lastName").value("Smith"))
                    .andExpect(jsonPath("$.phone").value("+9876543210"));

            verify(userProfileService, times(1)).updateCurrentUserProfile(any(UpdateProfileRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when firstName is blank")
        void updateCurrentUserProfile_BlankFirstName() throws Exception {
            // Given
            UpdateProfileRequest invalidRequest = UpdateProfileRequest.builder()
                    .firstName("")
                    .lastName("Doe")
                    .phone("+1234567890")
                    .dateOfBirth(LocalDate.of(1990, 1, 15))
                    .gender(Gender.MALE)
                    .address("123 Main St")
                    .build();

            // When & Then
            mockMvc.perform(put("/api/profile/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userProfileService, never()).updateCurrentUserProfile(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when firstName exceeds max length")
        void updateCurrentUserProfile_FirstNameTooLong() throws Exception {
            // Given
            String longFirstName = "a".repeat(101);
            UpdateProfileRequest invalidRequest = UpdateProfileRequest.builder()
                    .firstName(longFirstName)
                    .lastName("Doe")
                    .phone("+1234567890")
                    .dateOfBirth(LocalDate.of(1990, 1, 15))
                    .gender(Gender.MALE)
                    .address("123 Main St")
                    .build();

            // When & Then
            mockMvc.perform(put("/api/profile/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userProfileService, never()).updateCurrentUserProfile(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when phone number format is invalid")
        void updateCurrentUserProfile_InvalidPhoneFormat() throws Exception {
            // Given
            UpdateProfileRequest invalidRequest = UpdateProfileRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .phone("invalid-phone")
                    .dateOfBirth(LocalDate.of(1990, 1, 15))
                    .gender(Gender.MALE)
                    .address("123 Main St")
                    .build();

            // When & Then
            mockMvc.perform(put("/api/profile/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userProfileService, never()).updateCurrentUserProfile(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when date of birth is in future")
        void updateCurrentUserProfile_FutureDateOfBirth() throws Exception {
            // Given
            UpdateProfileRequest invalidRequest = UpdateProfileRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .phone("+1234567890")
                    .dateOfBirth(LocalDate.now().plusDays(1))
                    .gender(Gender.MALE)
                    .address("123 Main St")
                    .build();

            // When & Then
            mockMvc.perform(put("/api/profile/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(userProfileService, never()).updateCurrentUserProfile(any());
        }
    }

    @Nested
    @DisplayName("Update User Profile By ID Tests")
    class UpdateUserProfileByIdTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully update user profile by ID")
        void updateUserProfile_Success() throws Exception {
            // Given
            when(userProfileService.updateProfile(eq(sampleUserId), any(UpdateProfileRequest.class)))
                    .thenReturn(sampleProfileResponse);

            // When & Then
            mockMvc.perform(put("/api/profile/{userId}", sampleUserId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"));

            verify(userProfileService, times(1)).updateProfile(eq(sampleUserId), any(UpdateProfileRequest.class));
        }
    }

    @Nested
    @DisplayName("Upload Avatar Tests")
    class UploadAvatarTests {

        @Test
        @WithMockUser
        @DisplayName("Should successfully upload avatar")
        void uploadAvatar_Success() throws Exception {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "avatar.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            FileUploadResponse uploadResponse = FileUploadResponse.builder()
                    .fileUrl("https://example.com/avatars/avatar.jpg")
                    .fileName("avatar.jpg")
                    .fileSize(1024L)
                    .build();

            when(userProfileService.uploadCurrentUserAvatar(any(MockMultipartFile.class)))
                    .thenReturn(uploadResponse);

            // When & Then
            mockMvc.perform(multipart("/api/profile/me/avatar")
                            .file(mockFile)
                            .with(csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fileUrl").value("https://example.com/avatars/avatar.jpg"))
                    .andExpect(jsonPath("$.fileName").value("avatar.jpg"));

            verify(userProfileService, times(1)).uploadCurrentUserAvatar(any(MockMultipartFile.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when no file provided")
        void uploadAvatar_NoFile() throws Exception {
            // When & Then
            mockMvc.perform(multipart("/api/profile/me/avatar")
                            .with(csrf())
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest());

            verify(userProfileService, never()).uploadCurrentUserAvatar(any());
        }
    }

    @Nested
    @DisplayName("Admin Endpoints Tests")
    class AdminEndpointsTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should successfully get all users as admin")
        void getAllUsers_Success() throws Exception {
            // Given
            List<ProfileResponse> profiles = Arrays.asList(sampleProfileResponse);
            Page<ProfileResponse> profilePage = new PageImpl<>(profiles, PageRequest.of(0, 10), 1);

            when(userProfileService.getAllProfiles(any(Pageable.class))).thenReturn(profilePage);

            // When & Then
            mockMvc.perform(get("/api/profile/admin/users")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "createdAt")
                            .param("sortDir", "desc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].firstName").value("John"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(userProfileService, times(1)).getAllProfiles(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "PATIENT")
        @DisplayName("Should return 403 when non-admin tries to get all users")
        void getAllUsers_Forbidden() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/profile/admin/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(userProfileService, never()).getAllProfiles(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should successfully search users as admin")
        void searchUsers_Success() throws Exception {
            // Given
            List<ProfileResponse> profiles = Arrays.asList(sampleProfileResponse);
            Page<ProfileResponse> profilePage = new PageImpl<>(profiles, PageRequest.of(0, 10), 1);

            when(userProfileService.searchProfiles(eq("John"), any(Pageable.class))).thenReturn(profilePage);

            // When & Then
            mockMvc.perform(get("/api/profile/admin/users/search")
                            .param("keyword", "John")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "createdAt")
                            .param("sortDir", "desc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].firstName").value("John"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(userProfileService, times(1)).searchProfiles(eq("John"), any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "PATIENT")
        @DisplayName("Should return 403 when non-admin tries to search users")
        void searchUsers_Forbidden() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/profile/admin/users/search")
                            .param("keyword", "John")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(userProfileService, never()).searchProfiles(anyString(), any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle default pagination parameters")
        void getAllUsers_DefaultParameters() throws Exception {
            // Given
            List<ProfileResponse> profiles = Arrays.asList(sampleProfileResponse);
            Page<ProfileResponse> profilePage = new PageImpl<>(profiles, PageRequest.of(0, 10), 1);

            when(userProfileService.getAllProfiles(any(Pageable.class))).thenReturn(profilePage);

            // When & Then
            mockMvc.perform(get("/api/profile/admin/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Verify that default pagination parameters were used
            verify(userProfileService, times(1)).getAllProfiles(argThat(pageable ->
                    pageable.getPageNumber() == 0 &&
                            pageable.getPageSize() == 10 &&
                            pageable.getSort().getOrderFor("createdAt") != null &&
                            pageable.getSort().getOrderFor("createdAt").getDirection() == Sort.Direction.DESC
            ));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle ascending sort direction")
        void getAllUsers_AscendingSort() throws Exception {
            // Given
            List<ProfileResponse> profiles = Arrays.asList(sampleProfileResponse);
            Page<ProfileResponse> profilePage = new PageImpl<>(profiles, PageRequest.of(0, 10), 1);

            when(userProfileService.getAllProfiles(any(Pageable.class))).thenReturn(profilePage);

            // When & Then
            mockMvc.perform(get("/api/profile/admin/users")
                            .param("sortDir", "asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Verify that ascending sort was used
            verify(userProfileService, times(1)).getAllProfiles(argThat(pageable ->
                    pageable.getSort().getOrderFor("createdAt") != null &&
                            pageable.getSort().getOrderFor("createdAt").getDirection() == Sort.Direction.ASC
            ));
        }
    }

    @Nested
    @DisplayName("Content Type and Media Type Tests")
    class ContentTypeTests {

        @Test
        @WithMockUser
        @DisplayName("Should handle JSON content type correctly")
        void handleJsonContentType() throws Exception {
            // Given
            when(userProfileService.updateCurrentUserProfile(any(UpdateProfileRequest.class)))
                    .thenReturn(sampleProfileResponse);

            // When & Then
            mockMvc.perform(put("/api/profile/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sampleUpdateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", "application/json"));

            verify(userProfileService, times(1)).updateCurrentUserProfile(any(UpdateProfileRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should handle multipart form data correctly for avatar upload")
        void handleMultipartFormData() throws Exception {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    "test content".getBytes()
            );

            FileUploadResponse uploadResponse = FileUploadResponse.builder()
                    .fileUrl("https://example.com/test.jpg")
                    .build();

            when(userProfileService.uploadCurrentUserAvatar(any(MockMultipartFile.class)))
                    .thenReturn(uploadResponse);

            // When & Then
            mockMvc.perform(multipart("/api/profile/me/avatar")
                            .file(mockFile)
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(userProfileService, times(1)).uploadCurrentUserAvatar(any(MockMultipartFile.class));
        }
    }
}