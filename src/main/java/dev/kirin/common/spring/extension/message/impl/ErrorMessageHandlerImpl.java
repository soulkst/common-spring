package dev.kirin.common.spring.extension.message.impl;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.extension.message.ErrorMessageHandler;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Locale;

@Slf4j
public class ErrorMessageHandlerImpl implements ErrorMessageHandler {
    private static final String DEFAULT_CODE = "default";
    private final MessageSource messageSource;
    private final String errorTitleSuffix;
    private final String errorMessageSuffix;

    public ErrorMessageHandlerImpl(@NotNull MessageSource messageSource, String errorCodePrefix) {
        this(messageSource, errorCodePrefix, null, null);
    }

    @Builder
    ErrorMessageHandlerImpl(@NotNull MessageSource messageSource, String codePrefix, String titleSuffix, String messageSuffix) {
        this.messageSource = messageSource;

        String errorCodeFormat = StringUtil.ifEmptyBlank(codePrefix) + "{}";
        this.errorTitleSuffix = errorCodeFormat + StringUtil.ifEmptyBlank(titleSuffix);
        this.errorMessageSuffix = errorCodeFormat + StringUtil.ifEmptyBlank(messageSuffix);
    }

    @PostConstruct
    void postConstruct() {
        log.info("(postConstruct) Enabled ErrorMessageHandler. messageSource = {}", messageSource.getClass().getName());
    }

    @Override
    public <T extends Throwable> String getTitle(HttpServletRequest request, Class<T> throwable, Object[] args, String defaultMessage) {
        return getErrorMessage(this.errorTitleSuffix, throwable.getName(), args, defaultMessage, request.getLocale());
    }

    @Override
    public <T extends Throwable> String getDetail(HttpServletRequest request, Class<T> throwable, Object[] args, String defaultMessage) {
        return getErrorMessage(this.errorMessageSuffix, throwable.getName(), args, defaultMessage, request.getLocale());
    }

    private String getErrorMessage(String format, String code, Object[] args, String defaultValue, Locale locale) {
        String messageCode = StringUtil.format(format, code);
        String defaultMessage = getDefaultMessage(format, code, defaultValue, locale);
        String resultMessage = messageSource.getMessage(StringUtil.format(format, code), args, defaultMessage, locale);
        log.debug("(getErrorMessage) code = {}, locale = {}, args = {}, result = {}", messageCode, locale, args, resultMessage);
        return resultMessage;
    }

    private String getDefaultMessage(String format, String code, String defaultValue, Locale locale) {
        return messageSource.getMessage(StringUtil.format(format, DEFAULT_CODE), new Object[]{code}, defaultValue, locale);
    }
}
