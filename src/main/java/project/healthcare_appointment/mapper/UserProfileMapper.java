package project.healthcare_appointment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import project.healthcare_appointment.dto.request.user_profile_request.UpdateProfileRequest;
import project.healthcare_appointment.dto.response.user_profile_response.ProfileResponse;
import project.healthcare_appointment.model.UserProfile;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfileMapper {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "user.isActive", target = "isActive")
    ProfileResponse toProfileResponse(UserProfile userProfile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateProfileFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile userProfile);

    List<ProfileResponse> toProfileResponseList(List<UserProfile> userProfiles);

    default Page<ProfileResponse> toProfileResponsePage(Page<UserProfile> userProfilePage) {
        return userProfilePage.map(this::toProfileResponse);
    }
}
