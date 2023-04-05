package dev.kirin.common.spring.extension.message;

import dev.kirin.common.core.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;

public interface ErrorMessageHandler {
    default <T extends Throwable> String getTitle(HttpServletRequest request, T throwable) {
        return getTitle(request, throwable.getClass(), null, throwable.getLocalizedMessage());
    }

    default <T extends Throwable> String getTitle(HttpServletRequest request, T throwable, Object... args) {
        return getTitle(request, throwable.getClass(), args, throwable.getLocalizedMessage());
    }

    default <T extends Throwable> String getDetail(HttpServletRequest request, T throwable, Object... args) {
        return getDetail(request, throwable.getClass(), args, StringUtil.BLANK);
    }

    default <T extends Throwable> String getTitle(HttpServletRequest request, Class<T> throwable, String defaultValue) {
        return getTitle(request, throwable, null, defaultValue);
    }

    default <T extends Throwable> String getDetail(HttpServletRequest request, Class<T> throwable, Object... args) {
        return getDetail(request, throwable, args, StringUtil.BLANK);
    }

    <T extends Throwable> String getTitle(HttpServletRequest request, Class<T> throwable, Object[] args, String defaultMessage);
    <T extends Throwable> String getDetail(HttpServletRequest request, Class<T> throwable, Object[] args, String defaultMessage);
}
