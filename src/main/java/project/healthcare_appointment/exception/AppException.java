package project.healthcare_appointment.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private Object data;
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = null;
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.data = null;
    }

    public AppException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }

    public AppException(ErrorCode errorCode, String customMessage, Object data) {
        super(customMessage);
        this.errorCode = errorCode;
        this.data = data;
    }

    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = null;
    }

    public AppException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.data = null;
    }

    @Builder
    public AppException(ErrorCode errorCode, String message, Object data, Throwable cause) {
        super(message != null ? message : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = data;
    }

    public static AppException of(ErrorCode errorCode) {
        return new AppException(errorCode);
    }

    public static AppException of(ErrorCode errorCode, String message) {
        return new AppException(errorCode, message);
    }

    public static AppException of(ErrorCode errorCode, Object data) {
        return new AppException(errorCode, data);
    }

    public static AppException of(ErrorCode errorCode, String message, Object data) {
        return new AppException(errorCode, message, data);
    }

    public static AppException of(ErrorCode errorCode, Throwable cause) {
        return new AppException(errorCode, cause);
    }

    public static AppException of(ErrorCode errorCode, String message, Throwable cause) {
        return new AppException(errorCode, message, cause);
    }

}
