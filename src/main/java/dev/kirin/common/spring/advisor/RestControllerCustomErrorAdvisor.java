package dev.kirin.common.spring.advisor;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.exception.InvalidArgumentException;
import dev.kirin.common.spring.exception.NotFoundException;
import dev.kirin.common.spring.extension.message.ErrorMessageHandler;
import dev.kirin.common.spring.model.vo.ApiErrorVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestControllerCustomErrorAdvisor {
    private final ErrorMessageHandler errorMessageHandler;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotfoundException(HttpServletRequest request, NotFoundException e) {
        log.debug("(handleNotfoundException) stack-trace", e);
        Object[] messageArgs = new Object[]{e.getId(), e.getDomain()};
        String title = errorMessageHandler.getTitle(request, e, messageArgs);
        String detail = errorMessageHandler.getDetail(request, e, messageArgs);
        ApiErrorVo result = ApiErrorVo.of(request, e, title, detail);
        result.setMore(e.getMore());

        log.error("(handleNotfoundException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return StringUtil.BLANK;
    }

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidArgumentException(HttpServletRequest request, InvalidArgumentException e) {
        log.debug("(handleInvalidArgumentException) stack-trace", e);
        Object[] messageArgs = new Object[]{e.getDomain() + InvalidArgumentException.DELIMITER + e.getLocation(), e.getValue()};
        String title = errorMessageHandler.getTitle(request, e);
        String detail = errorMessageHandler.getDetail(request, e, messageArgs);
        ApiErrorVo result = ApiErrorVo.of(request, e, title, detail);
        result.setMore(e.getMore());

        log.error("(handleInvalidArgumentException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return StringUtil.BLANK;
    }
}
