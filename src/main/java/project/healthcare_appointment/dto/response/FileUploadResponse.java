package project.healthcare_appointment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "File upload response DTO")
public class FileUploadResponse {
    @Schema(description = "Upload success status")
    Boolean success;

    @Schema(description = "Uploaded file URL")
    String fileUrl;

    @Schema(description = "Original file name")
    String fileName;

    @Schema(description = "File size in bytes")
    Long fileSize;

    @Schema(description = "Upload message")
    String message;
}
