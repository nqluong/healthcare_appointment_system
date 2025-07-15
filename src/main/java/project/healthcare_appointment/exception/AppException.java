package project.healthcare_appointment.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;
    private Object data;
    private Map<String, Object> properties;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.properties = new HashMap<>();
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.properties = new HashMap<>();
    }

    public AppException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
        this.properties = new HashMap<>();
    }

    public AppException(ErrorCode errorCode, String customMessage, Object data) {
        super(customMessage);
        this.errorCode = errorCode;
        this.data = data;
        this.properties = new HashMap<>();
    }

    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.properties = new HashMap<>();
    }

    public AppException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.properties = new HashMap<>();
    }

    // Builder pattern for complex exceptions
    public static AppExceptionBuilder builder(ErrorCode errorCode) {
        return new AppExceptionBuilder(errorCode);
    }

    // Add property to exception
    public AppException addProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    // Get property from exception
    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    public static class AppExceptionBuilder {
        private final ErrorCode errorCode;
        private String customMessage;
        private Object data;
        private Throwable cause;
        private Map<String, Object> properties = new HashMap<>();

        public AppExceptionBuilder(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }

        public AppExceptionBuilder message(String message) {
            this.customMessage = message;
            return this;
        }

        public AppExceptionBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public AppExceptionBuilder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public AppExceptionBuilder property(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }

        public AppException build() {
            AppException exception;
            if (cause != null) {
                exception = customMessage != null
                        ? new AppException(errorCode, customMessage, cause)
                        : new AppException(errorCode, cause);
            } else {
                exception = customMessage != null
                        ? new AppException(errorCode, customMessage, data)
                        : new AppException(errorCode, data);
            }
            exception.properties.putAll(this.properties);
            return exception;
        }

    }
}
