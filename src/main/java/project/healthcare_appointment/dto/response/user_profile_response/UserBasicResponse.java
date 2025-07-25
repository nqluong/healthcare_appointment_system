package project.healthcare_appointment.dto.response.user_profile_response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Basic user information for doctor response")
public class UserBasicResponse {

    @Schema(description = "User's unique identifier")
    UUID id;

    @Schema(description = "Username", example = "dr.john.doe")
    String username;

    @Schema(description = "Email address", example = "john.doe@hospital.com")
    String email;

    @Schema(description = "User role", example = "DOCTOR")
    String role;

    @Schema(description = "Account active status", example = "true")
    Boolean isActive;

}
