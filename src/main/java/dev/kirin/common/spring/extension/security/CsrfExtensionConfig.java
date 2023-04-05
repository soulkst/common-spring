package dev.kirin.common.spring.extension.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Slf4j
@Configuration
@ConditionalOnBean(annotation = EnableWebSecurity.class)
public class CsrfExtensionConfig {
    @Bean
    CsrfTokenRepository csrfTokenRepository(SecurityExtensionProperties properties) {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(properties.getCsrf().getHeaderName());
        repository.setParameterName(properties.getCsrf().getParameterName());
        repository.setSessionAttributeName(properties.getCsrf().getSessionAttributeName());
        log.info("(csrfTokenRepository) csrf names = {}", properties.getCsrf());
        return repository;
    }
}
