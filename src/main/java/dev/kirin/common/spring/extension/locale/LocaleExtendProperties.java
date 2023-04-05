package dev.kirin.common.spring.extension.locale;

import dev.kirin.common.spring.conditional.ConditionalOnPropertyPresent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Setter(AccessLevel.MODULE)
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.locale")
@ConditionalOnPropertyPresent("app.locale")
public class LocaleExtendProperties {
    private Locale defaultLocale = Locale.getDefault();
    private String parameterName;
}
