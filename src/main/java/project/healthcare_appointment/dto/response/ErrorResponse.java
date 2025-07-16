package project.healthcare_appointment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private Object data;
    private String timestamp;
    private String path;

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .success(false)
                .message(errorCode.getMessage())
                .errorCode(errorCode.getCode())
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String customMessage, String path) {
        return ErrorResponse.builder()
                .success(false)
                .message(customMessage)
                .errorCode(errorCode.getCode())
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }

    public static ErrorResponse of(AppException ex, String path) {
        return ErrorResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode().getCode())
                .data(ex.getData())
                .timestamp(LocalDateTime.now().toString())
                .path(path)
                .build();
    }
}
