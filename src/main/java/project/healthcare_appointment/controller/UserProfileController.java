package project.healthcare_appointment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.healthcare_appointment.dto.request.user_profile_request.UpdateProfileRequest;
import project.healthcare_appointment.dto.response.FileUploadResponse;
import project.healthcare_appointment.dto.response.user_profile_response.ProfileResponse;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.service.user_service.UserProfileService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "APIs for user profile management")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieve the profile information of the currently authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<ProfileResponse> getCurrentUserProfile() {

        ProfileResponse profile = userProfileService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get user profile by ID",
            description = "Retrieve profile information of a specific user (Admin only or own profile)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<ProfileResponse> getUserProfile(
            @Parameter(description = "User ID") @PathVariable UUID userId) {

        ProfileResponse profile = userProfileService.getProfile(userId);

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(
            summary = "Update current user profile",
            description = "Update the profile information of the currently authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<ProfileResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequest request
            ) {

        ProfileResponse updatedProfile = userProfileService.updateCurrentUserProfile( request);

        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/{userId}")
    @Operation(
            summary = "Update user profile by ID",
            description = "Update profile information of a specific user (accessible by user themselves or admin)",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<ProfileResponse> updateUserProfile(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileRequest request) {

        ProfileResponse updatedProfile = userProfileService.updateProfile(userId, request);

        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload user avatar",
            description = "Upload an avatar image for the currently authenticated user",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponse(responseCode = "200", description = "Avatar uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file or file too large")
    public ResponseEntity<FileUploadResponse> uploadAvatar(
            @Parameter(description = "Avatar image file")
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }

        FileUploadResponse uploadResponse = userProfileService.uploadCurrentUserAvatar( file);

        return ResponseEntity.ok(uploadResponse);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all user profiles (Admin only)",
            description = "Retrieve paginated list of all user profiles - Admin access required",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<Page<ProfileResponse>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProfileResponse> users = userProfileService.getAllProfiles(pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Search user profiles (Admin only)",
            description = "Search user profiles by keyword - Admin access required",
            security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<Page<ProfileResponse>> searchUsers(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProfileResponse> users = userProfileService.searchProfiles(keyword, pageable);

        return ResponseEntity.ok(users);
    }
}
