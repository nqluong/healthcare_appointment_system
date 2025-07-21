package project.healthcare_appointment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.healthcare_appointment.dto.request.user_profile_request.UpdateProfileRequest;
import project.healthcare_appointment.dto.response.FileUploadResponse;
import project.healthcare_appointment.dto.response.user_profile_response.ProfileResponse;
import project.healthcare_appointment.enums.FileUploadType;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.mapper.UserProfileMapper;
import project.healthcare_appointment.model.UserProfile;
import project.healthcare_appointment.repository.UserProfileRepository;
import project.healthcare_appointment.repository.UserRepository;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {

    UserRepository userRepository;
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    FileUploadService fileUploadService;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(UUID userId) {
        UserProfile profile = getUserProfileByUserId(userId);
        return userProfileMapper.toProfileResponse(profile);
    }

    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        try {
            UserProfile profile = getUserProfileByUserId(userId);
            userProfileMapper.updateProfileFromRequest(request, profile);

            UserProfile savedProfile = userProfileRepository.save(profile);
            log.info("Profile updated successfully for user: {}", userId);

            return userProfileMapper.toProfileResponse(savedProfile);
        } catch (Exception e) {
            log.error("Failed to update profile for user: {}", userId, e);
            throw new AppException(ErrorCode.PROFILE_UPDATE_FAILED, e);
        }
    }

    public FileUploadResponse uploadAvatar(UUID userId, MultipartFile file) {
        try {
            UserProfile profile = getUserProfileByUserId(userId);

            if (profile.getAvatarUrl() != null) {
                fileUploadService.deleteFile(profile.getAvatarUrl());
            }
            // Upload new avatar
            FileUploadResponse uploadResponse = fileUploadService.uploadFile(file, FileUploadType.AVATAR);

            // Update profile with new avatar URL
            profile.setAvatarUrl(uploadResponse.getFileUrl());
            userProfileRepository.save(profile);

            log.info("Avatar uploaded successfully for user: {}", userId);
            return uploadResponse;

        } catch (Exception e) {
            log.error("Failed to upload avatar for user: {}", userId, e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }



    @Transactional(readOnly = true)
    public Page<ProfileResponse> getAllProfiles(Pageable pageable) {
        Page<UserProfile> profilePage = userProfileRepository.findAllWithUser(pageable);
        return userProfileMapper.toProfileResponsePage(profilePage);
    }

    @Transactional(readOnly = true)
    public Page<ProfileResponse> searchProfiles(String keyword, Pageable pageable) {
        Page<UserProfile> profilePage = userProfileRepository.searchProfiles(keyword, pageable);
        return userProfileMapper.toProfileResponsePage(profilePage);
    }

    private UserProfile getUserProfileByUserId(UUID userId) {
        return userProfileRepository.findByUserIdWithUser(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
    }
}
