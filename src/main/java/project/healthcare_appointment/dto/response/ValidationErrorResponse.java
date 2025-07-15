package project.healthcare_appointment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationErrorResponse {
    boolean success;
    String message;
    String errorCode;
    Map<String, String> fieldErrors;
    String timestamp;
    String path;
}
