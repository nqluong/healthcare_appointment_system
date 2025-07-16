package project.healthcare_appointment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication Errors
    INVALID_CREDENTIALS("AUTH001", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_INACTIVE("AUTH002", "Account is inactive", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH003", "Account is locked", HttpStatus.LOCKED),
    EMAIL_NOT_VERIFIED("AUTH004", "Email is not verified", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("AUTH005","You must be logged in to logout" ,HttpStatus.UNAUTHORIZED ),

    // JWT Token Errors
    TOKEN_EXPIRED("JWT001", "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_MALFORMED("JWT002", "Token is malformed", HttpStatus.UNAUTHORIZED),
    TOKEN_SIGNATURE_INVALID("JWT003", "Token signature is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_UNSUPPORTED("JWT004", "Token is not supported", HttpStatus.UNAUTHORIZED),
    TOKEN_CLAIMS_EMPTY("JWT005", "Token claims are empty", HttpStatus.UNAUTHORIZED),
    TOKEN_GENERATION_ERROR("JWT006", "Failed to generate token", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_REFRESH_ERROR("JWT007", "Failed to refresh token", HttpStatus.UNAUTHORIZED),
    TOKEN_PARSE_ERROR("JWT008", "Failed to parse token", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("JWT009", "Invalid refresh token", HttpStatus.UNAUTHORIZED),

    // User Errors
    USER_NOT_FOUND("USER001", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER002", "User already exists", HttpStatus.CONFLICT),
    USERNAME_TAKEN("USER003", "Username is already taken", HttpStatus.CONFLICT),
    EMAIL_TAKEN("USER004", "Email is already taken", HttpStatus.CONFLICT),
    PHONE_TAKEN("USER005", "Phone number is already taken", HttpStatus.CONFLICT),

    // Validation Errors
    VALIDATION_ERROR("VAL001", "Validation failed", HttpStatus.BAD_REQUEST),
    REQUIRED_FIELD_MISSING("VAL002", "Required field is missing", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT("VAL003", "Invalid format", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK("VAL004", "Password is too weak", HttpStatus.BAD_REQUEST),

    // Access Control Errors
    ACCESS_DENIED("ACC001", "Access denied", HttpStatus.FORBIDDEN),
    INSUFFICIENT_PERMISSIONS("ACC002", "Insufficient permissions", HttpStatus.FORBIDDEN),
    ROLE_NOT_AUTHORIZED("ACC003", "Role is not authorized for this action", HttpStatus.FORBIDDEN),

    // System Errors
    INTERNAL_ERROR("SYS001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("SYS002", "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYS003", "Service is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    // Business Logic Errors
    APPOINTMENT_CONFLICT("BIZ001", "Appointment conflict", HttpStatus.CONFLICT),
    INVALID_OPERATION("BIZ002", "Invalid operation", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_AVAILABLE("BIZ003", "Resource is not available", HttpStatus.NOT_FOUND),

    // File Upload Errors
    FILE_TOO_LARGE("FILE001", "File size exceeds limit", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("FILE002", "Invalid file type", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_ERROR("FILE003", "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
