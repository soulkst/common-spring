package dev.kirin.common.spring.exception;

import dev.kirin.common.core.utils.StringUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RestApiException {
    private static final String MESSAGE_FORMAT = "Not found entity. domain = {}, id = {}";
    private final String domain;
    private final Object id;

    public NotFoundException(String domain, Object id) {
        super(StringUtil.format(MESSAGE_FORMAT, domain, id), HttpStatus.NOT_FOUND);
        this.domain = domain;
        this.id = id;
    }
}
