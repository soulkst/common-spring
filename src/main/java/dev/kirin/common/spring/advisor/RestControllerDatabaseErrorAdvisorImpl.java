package dev.kirin.common.spring.advisor;

import dev.kirin.common.spring.advisor.database.AbstractDatabaseErrorDetailAdvisor;
import dev.kirin.common.spring.extension.message.ErrorMessageHandler;
import dev.kirin.common.spring.model.vo.ApiErrorVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestControllerDatabaseErrorAdvisorImpl {
    private final ErrorMessageHandler errorMessageHandler;
    private final AbstractDatabaseErrorDetailAdvisor detailAdvisor;

    @PostConstruct
    void postConstruct() {
        log.info("(default-database-advisor) Enabled");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorVo handleDataIntegrityViolationException(HttpServletRequest request, DuplicateKeyException e) {
        log.debug("(handleDataIntegrityViolationException) stack-trace", e);
        String title = errorMessageHandler.getTitle(request, e);

        ApiErrorVo result = ApiErrorVo.conflict(request, e, title);
        result.setMore(e.getLocalizedMessage());

        log.error("(handleDataIntegrityViolationException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);

        return result;
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiErrorVo handleEmptyResultDataAccessException(HttpServletRequest request, EmptyResultDataAccessException e) {
        log.debug("(handleEmptyResultDataAccessException) stack-trace", e);
        String title = errorMessageHandler.getTitle(request, e);

        ApiErrorVo result = ApiErrorVo.noContent(request, e, title);
        result.setMore(e.getLocalizedMessage());
        log.error("(handleEmptyResultDataAccessException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return result;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorVo> handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        log.debug("(handleDataIntegrityViolationException) stack-trace", e);
        ResponseEntity<ApiErrorVo> detailResponse = detailAdvisor.handleDataIntegrityViolationException(request, e);
        if(detailResponse != null) {
            return detailResponse;
        }

        String title = errorMessageHandler.getTitle(request, e);

        ApiErrorVo result = ApiErrorVo.noContent(request, e, title);
        log.error("(handleDataIntegrityViolationException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return ResponseEntity.ok(new ApiErrorVo());
    }
}
