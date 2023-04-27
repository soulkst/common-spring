package dev.kirin.common.spring.advisor;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.advisor.vo.BindExceptionDetailVo;
import dev.kirin.common.spring.advisor.vo.ConstraintViolationDetailVo;
import dev.kirin.common.spring.advisor.vo.InvalidJsonFormatDetailVo;
import dev.kirin.common.spring.advisor.vo.RequestDataDetailVo;
import dev.kirin.common.spring.extension.message.ErrorMessageHandler;
import dev.kirin.common.spring.model.vo.ApiErrorVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RestControllerAdvice
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RestControllerDefaultErrorAdvisor {
    private final ErrorMessageHandler errorMessageHandler;

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorVo handleBindException(HttpServletRequest request, BindException e) {
        log.debug("(handleBindException) stack-trace", e);
        BindingResult bindingResult = e.getBindingResult();

        String title = errorMessageHandler.getTitle(request, BindException.class, e.getLocalizedMessage());
        String detail = StringUtil.BLANK;
        if(bindingResult.hasFieldErrors()) {
            String fields = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getField)
                    .collect(Collectors.joining(","));
            detail = errorMessageHandler.getDetail(request, BindException.class, fields);
        }
        ApiErrorVo result = ApiErrorVo.badRequest(request, e, title, detail);
        result.setMore(BindExceptionDetailVo.ofAll(bindingResult.getAllErrors()));
        log.error("(handleBindException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return result;
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorVo handleConstraintViolationException(HttpServletRequest request, javax.validation.ConstraintViolationException e) {
        log.debug("(handleConstraintViolationException) stack-trace", e);
        String fields = e.getConstraintViolations()
                .stream()
                .map(constraintViolation -> constraintViolation.getPropertyPath().toString())
                .collect(Collectors.joining(","));
        String title = errorMessageHandler.getTitle(request, e);
        String detail = errorMessageHandler.getDetail(request, e, fields);
        ApiErrorVo result = ApiErrorVo.badRequest(request, e, title, detail);
        result.setMore(ConstraintViolationDetailVo.ofAll(e.getConstraintViolations()));
        log.error("(handleConstraintViolationException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return result;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorVo handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        log.debug("(handleMethodArgumentTypeMismatchException) stack-trace", e);
        String fieldName = e.getName();
        String definedTypeClass = e.getRequiredType().getName();
        String requestedTypeClass = e.getValue().getClass().getSimpleName();
        String title = errorMessageHandler.getTitle(request, e);
        String detail = errorMessageHandler.getDetail(request, e, fieldName, definedTypeClass, requestedTypeClass);
        ApiErrorVo result = ApiErrorVo.badRequest(request, e, title, detail);
        log.error("(handleMethodArgumentTypeMismatchException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return result;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorVo handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        log.debug("(handleMissingServletRequestParameterException) stack-trace", e);
        String parameterName = e.getParameterName();
        String title = errorMessageHandler.getTitle(request, e);
        String detail = errorMessageHandler.getDetail(request, e, parameterName);
        ApiErrorVo result = ApiErrorVo.badRequest(request, e, title, detail);
        log.error("(handleMissingServletRequestParameterException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return result;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorVo handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.debug("(handleHttpMessageNotReadableException) stack-trace", e);
        if(e.getRootCause() instanceof InvalidFormatException) {
            // invalid json format
            InvalidFormatException ie = (InvalidFormatException) e.getRootCause();
            String title = errorMessageHandler.getTitle(request, ie);
            String detail = ie.getLocalizedMessage();
            ApiErrorVo result = ApiErrorVo.badRequest(request, ie, title, detail);
            result.setMore(InvalidJsonFormatDetailVo.of(ie));
            log.error("(handleHttpMessageNotReadableException - InvalidFormatException) uri = {}, cause = {}, response = {}", request.getRequestURI(), ie.getLocalizedMessage(), result);
            return result;
        }

        String title = errorMessageHandler.getTitle(request, e);
        HttpInputMessage httpInputMessage = e.getHttpInputMessage();
        String body = null;
        try {
            body = StreamUtils.copyToString(httpInputMessage.getBody(), Charset.forName(request.getCharacterEncoding()));
        } catch (Exception e1) {
            log.error("(handleHttpMessageNotReadableException) Fail body stream to string. cause = {}", e1.getLocalizedMessage());
        }
        ApiErrorVo result = ApiErrorVo.badRequest(request, e, title);
        result.setMore(RequestDataDetailVo.of(httpInputMessage.getHeaders(), body));
        log.error("(handleHttpMessageNotReadableException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return result;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorVo handleHttpMediaTypeNotSupportedException(HttpServletRequest request, HttpMediaTypeNotSupportedException e) {
        log.debug("(handleHttpMediaTypeNotSupportedException) stack-trace", e);
        String title = errorMessageHandler.getTitle(request, e);
        String detail = errorMessageHandler.getDetail(request, e, e.getContentType());

        List<String> supportMediaTypes = e.getSupportedMediaTypes()
                .stream()
                .map(MediaType::toString)
                .collect(Collectors.toList());

        ApiErrorVo result = ApiErrorVo.badRequest(request, e, title, detail);
        result.setMore(supportMediaTypes);
        log.error("(handleHttpMediaTypeNotSupportedException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        return result;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorVo handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.debug("(handleHttpRequestMethodNotSupportedException) stack-trace", e);
        String title = errorMessageHandler.getTitle(request, e);
        String detail = errorMessageHandler.getDetail(request, e, e.getMethod());

        ApiErrorVo result = ApiErrorVo.notFound(request, e, title, detail);
        log.error("(handleHttpRequestMethodNotSupportedException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        result.setMore(e.getLocalizedMessage());
        return result;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorVo handleUnknown(HttpServletRequest request, Exception e) {
        log.debug("(handleUnknown) stack-trace", e);
        String title = errorMessageHandler.getTitle(request, e);
        String detail = errorMessageHandler.getDetail(request, e);

        ApiErrorVo result = ApiErrorVo.unknown(request, e, title, detail);
        log.error("(handleUnknown) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        result.setMore(e.getLocalizedMessage());
        return result;
    }
}
