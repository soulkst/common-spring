package dev.kirin.common.spring.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidArgumentException extends RestApiException {
    public static final String DELIMITER = "#";

    private final String domain;
    private final String location;
    private final Object value;

    public InvalidArgumentException(String domain, String location) {
        super("Invalid argument requested. at = " + domain + DELIMITER + location, HttpStatus.BAD_REQUEST);
        this.domain = domain;
        this.location = location;
        this.value = null;
    }

    public InvalidArgumentException(String domain, String location, Object value) {
        super("Invalid argument requested. at = " + domain + DELIMITER + location, HttpStatus.BAD_REQUEST);
        this.domain = domain;
        this.location = location;
        this.value = value;
    }
}
