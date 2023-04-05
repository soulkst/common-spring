package dev.kirin.common.spring.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class RestApiException extends RuntimeException {
    @Getter
    private final HttpStatus httpStatus;

    public Object getMore() {
        return null;
    }

    public RestApiException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public RestApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public RestApiException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public RestApiException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public RestApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus httpStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.httpStatus = httpStatus;
    }

    public String getResponseMessage() {
        return getLocalizedMessage();
    }
}
