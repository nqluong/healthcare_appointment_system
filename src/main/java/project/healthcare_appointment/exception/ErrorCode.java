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
    UNAUTHENTICATED("AUTH006","Authentication required", HttpStatus.UNAUTHORIZED),

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
    // Time Validation
    INVALID_DATE_RANGE("VAL007", "Start date must be before or equal to end date", HttpStatus.BAD_REQUEST),
    INVALID_TIME_RANGE("VAL006", "Start time must be before end time", HttpStatus.BAD_REQUEST),

    // Access Control Errors
    ACCESS_DENIED("ACC001", "Access denied", HttpStatus.FORBIDDEN),
    INSUFFICIENT_PERMISSIONS("ACC002", "Insufficient permissions", HttpStatus.FORBIDDEN),
    ROLE_NOT_AUTHORIZED("ACC003", "Role is not authorized for this action", HttpStatus.FORBIDDEN),

    // System Errors
    INTERNAL_ERROR("SYS001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("SYS002", "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYS003", "Service is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    INVALID_ARGUMENT("SYS004", "Invalid argument provided", HttpStatus.BAD_REQUEST),

    // Business Logic Errors
    APPOINTMENT_CONFLICT("BIZ001", "Appointment conflict", HttpStatus.CONFLICT),
    INVALID_OPERATION("BIZ002", "Invalid operation", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_AVAILABLE("BIZ003", "Resource is not available", HttpStatus.NOT_FOUND),

    // File Upload Errors
    FILE_UPLOAD_FAILED("FILE_001", "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE("FILE_002", "Invalid file type", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("FILE_003", "File size exceeded maximum limit", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND("FILE_004", "File not found", HttpStatus.NOT_FOUND),
    FILE_DELETE_FAILED("FILE_005", "Failed to delete file", HttpStatus.INTERNAL_SERVER_ERROR),

    // Profile Errors

    PROFILE_NOT_FOUND("PROFILE_001", "User profile not found", HttpStatus.NOT_FOUND),
    PROFILE_UPDATE_FAILED("PROFILE_002", "Failed to update profile",HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_PROFILE_ACCESS("PROFILE_003", "Unauthorized access to profile", HttpStatus.FORBIDDEN),

    //Specific Errors
    SPECIALIST_NOT_FOUND("SPEC_001", "Specialist not found", HttpStatus.NOT_FOUND),
    SPECIALTY_NAME_DUPLICATED("SPEC_002", "Specialty with name already exists", HttpStatus.CONFLICT),

    //Doctor Errors
    DOCTOR_NOT_FOUND("DOCTOR_001", "Doctor not found", HttpStatus.NOT_FOUND),
    DOCTOR_LICENSE_DUPLICATED("DOCTOR_002", "Doctor with license number already exists", HttpStatus.CONFLICT),

    //Schedule Errors
    SLOT_NOT_FOUND("SLOT_001", "Available slot not found", HttpStatus.NOT_FOUND),
    DOCTOR_SCHEDULE_NOT_FOUND("SLOT_002", "No active schedules found for doctor", HttpStatus.NOT_FOUND),
    SLOT_ALREADY_EXISTS("SLOT_003", "Slot already exists for this date and time", HttpStatus.CONFLICT),
    SLOT_BOOKED_CANNOT_DELETE("SLOT_004", "Cannot delete slot that has been booked", HttpStatus.BAD_REQUEST),
    SLOT_DOES_NOT_BELONG_TO_DOCTOR("SLOT_005", "Slot does not belong to this doctor", HttpStatus.BAD_REQUEST),
    SCHEDULE_ALREADY_EXISTS("SLOT_006", "Schedule already exists for this day of week", HttpStatus.CONFLICT),
    SCHEDULE_NOT_FOUND("SLOT_007", "Schedule not found for this doctor", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_ACTIVE("SLOT_008", "Schedule is not active", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_OWNED("SLOT_009", "Schedule does not belong to this doctor", HttpStatus.BAD_REQUEST),
    SLOT_NOT_AVAILABLE("SLOT_010", "Slot is not available for booking", HttpStatus.BAD_REQUEST),
    SLOT_EXPIRED("SLOT_011", "Slot has expired and cannot be booked", HttpStatus.BAD_REQUEST),

    DUPLICATE_APPOINTMENT("APPT_001", "Patient already has an appointment at this time", HttpStatus.CONFLICT),
    PATIENT_NOT_FOUND("PATIENT_001", "Patient not found", HttpStatus.NOT_FOUND),

    // Appointment cancellation errors
    APPOINTMENT_NOT_FOUND("APPT_002", "Appointment not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOT_CANCELLABLE("APPT_003", "Appointment cannot be cancelled", HttpStatus.BAD_REQUEST),
    CANCELLATION_TOO_LATE("APPT_004", "Cannot cancel appointment less than 1 day before appointment date", HttpStatus.BAD_REQUEST),
    APPOINTMENT_ALREADY_CANCELLED("APPT_005", "Appointment is already cancelled", HttpStatus.BAD_REQUEST),
    APPOINTMENT_ALREADY_COMPLETED("APPT_006", "Cannot cancel completed appointment", HttpStatus.BAD_REQUEST),
    APPOINTMENT_NOT_APPROVABLE("APPOINTMENT_007", "Appointment cannot be approved", HttpStatus.BAD_REQUEST),
    APPOINTMENT_NOT_REJECTABLE("APPOINTMENT_008", "Appointment cannot be rejected", HttpStatus.BAD_REQUEST),
    APPOINTMENT_EXPIRED("APPOINTMENT_009", "Appointment is in the past", HttpStatus.BAD_REQUEST),

    APPOINTMENT_ALREADY_APPROVED("APPOINTMENT_010", "Appointment is already approved", HttpStatus.BAD_REQUEST),
    APPOINTMENT_ALREADY_REJECTED("APPOINTMENT_011", "Appointment is already rejected", HttpStatus.BAD_REQUEST),
    INVALID_APPOINTMENT_STATUS("APPOINTMENT_012", "Invalid appointment status for this operation", HttpStatus.BAD_REQUEST),
    APPOINTMENT_DATE_INVALID("APPOINTMENT_013", "Appointment date is invalid", HttpStatus.BAD_REQUEST),
    DOCTOR_SLOT_NOT_FOUND("APPOINTMENT_014", "Doctor available slot not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_FILTER_INVALID("APPOINTMENT_015", "Invalid filter parameters provided", HttpStatus.BAD_REQUEST),
    APPOINTMENT_ACCESS_DENIED("APPOINTMENT_016", "Access denied to appointment", HttpStatus.FORBIDDEN),
    APPOINTMENT_UPDATE_FAILED("APPOINTMENT_017", "Failed to update appointment", HttpStatus.BAD_REQUEST),
    SLOT_UPDATE_FAILED("APPOINTMENT_018", "Failed to update doctor slot availability", HttpStatus.BAD_REQUEST),;
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
