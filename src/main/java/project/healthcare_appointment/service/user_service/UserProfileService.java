package project.healthcare_appointment.service.user_service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import project.healthcare_appointment.dto.request.user_profile_request.UpdateProfileRequest;
import project.healthcare_appointment.dto.response.FileUploadResponse;
import project.healthcare_appointment.dto.response.user_profile_response.ProfileResponse;

import java.util.UUID;

public interface UserProfileService {

    ProfileResponse getCurrentUserProfile();

    ProfileResponse updateCurrentUserProfile(UpdateProfileRequest request);

    FileUploadResponse uploadCurrentUserAvatar(MultipartFile file);

    ProfileResponse getProfile(UUID userId);

    Page<ProfileResponse> getAllProfiles(Pageable pageable);

    ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);

    FileUploadResponse uploadAvatar(UUID userId, MultipartFile file);

    Page<ProfileResponse> searchProfiles(String keyword, Pageable pageable);

}
