package dev.kirin.common.spring.extension.security;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.annotation.PostConstruct;

@Setter(AccessLevel.MODULE)
@Getter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "spring.security.extension")
public class SecurityExtensionProperties {
    private Csrf csrf;

    @PostConstruct
    void postConstruct() {
        if(this.csrf == null) {
            this.csrf = new Csrf();
        }
    }

    @Setter(AccessLevel.MODULE)
    @Getter
    @NoArgsConstructor
    @ToString
    public static class Csrf {
        public static final String DEFAULT_HEADER_NAME = "X-CSRF-TOKEN";
        public static final String DEFAULT_PARAMETER_NAME = "_csrf";
        public static final String DEFAULT_SESSION_ATTRIBUTE_NAME = HttpSessionCsrfTokenRepository.class.getName()
                .concat(".CSRF_TOKEN");

        private String headerName = DEFAULT_HEADER_NAME;
        private String parameterName = DEFAULT_PARAMETER_NAME;
        private String sessionAttributeName = DEFAULT_SESSION_ATTRIBUTE_NAME;
    }
}
