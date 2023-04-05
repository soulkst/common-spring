package dev.kirin.common.spring.extension.message;

import dev.kirin.common.spring.conditional.ConditionalOnPropertyPresent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Slf4j
@Setter(AccessLevel.MODULE)
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.messages.extension")
@ConditionalOnPropertyPresent(value = "spring.messages.extension")
public class MessageExtensionProperties {
    private Locale defaultLocale = Locale.getDefault();
    private String basePath = "messages";
    private LocaleStrategy strategy = LocaleStrategy.NAME;
    private ErrorMessage errorMessage;

    @Setter(AccessLevel.MODULE)
    @Getter
    @NoArgsConstructor
    @ToString
    public static class ErrorMessage {
        private String codePrefix;
        private String messageSuffix;
        private String titleSuffix;
    }

    public enum LocaleStrategy {
        NAME, PATH
    }
}
