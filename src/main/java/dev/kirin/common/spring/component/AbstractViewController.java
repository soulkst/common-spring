package dev.kirin.common.spring.component;

import dev.kirin.common.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractViewController {
    private static final String REDIRECT_URL_PATTERN = "redirect:{}{}";

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    protected String getRedirectPath(String uri) {
        return StringUtil.format(REDIRECT_URL_PATTERN, contextPath, uri);
    }
}
