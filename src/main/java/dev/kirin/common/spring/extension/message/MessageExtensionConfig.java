package dev.kirin.common.spring.extension.message;

import dev.kirin.common.core.utils.FileUtil;
import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.extension.message.impl.ErrorMessageHandlerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MessageExtensionConfig {
    private final ResourceLoader resourceLoader;
    @PostConstruct
    void postConstruct() {
        log.info("(postConstruct) Enabled message extension");
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    @ConditionalOnMissingBean(MessageSourceAutoConfiguration.class)
    MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(MessageSourceAutoConfiguration.class)
    MessageSource messageSource(MessageSourceProperties properties) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        if (StringUtils.hasText(properties.getBasename())) {
            String[] files = getBasenameHierarchy(properties.getBasename() ,resourceLoader);
            log.debug("(messageSource) files = {}", Arrays.toString(files));
            messageSource.setBasenames(files);
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }

    @Bean
    ErrorMessageHandler errorMessageHandler(MessageSourceProperties properties, @Nullable MessageExtensionProperties extensionProperties) {
        MessageSource messageSource = messageSource(properties);
        if(extensionProperties == null) {
            return new ErrorMessageHandlerImpl(messageSource, null);
        }
        MessageExtensionProperties.ErrorMessage errorMessage = extensionProperties.getErrorMessage();
        log.info("(errorMessageHandler) bean created. config = {}", errorMessage);
        return ErrorMessageHandlerImpl.builder()
                .messageSource(messageSource)
                .codePrefix(errorMessage.getCodePrefix())
                .messageSuffix(errorMessage.getMessageSuffix())
                .titleSuffix(errorMessage.getTitleSuffix())
                .build();
    }

    private String[] getBasenameHierarchy(String basename, ResourceLoader resourceLoader) {
        String locales = Arrays.stream(Locale.getAvailableLocales())
                .map(locale -> {
                    String localeString = locale.toString();
                    if(StringUtils.hasText(localeString)) {
                        return "_" + localeString;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("|"));
        String filterPattern = ".*(?<!" + locales + ")\\.(?i)(xml|properties)$";
        log.debug("(getBasenameHierarchy) filter pattern = {}", filterPattern);
        return StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(basename))
                .stream()
                .map(item -> {
                    try {
                        File file = resourceLoader.getResource(item).getFile();
                        if (file.exists() && file.isDirectory()) {
                            log.debug("(getBasenameHierarchy) item is directory. item = {}", item);
                            return FileUtil.findAll(file, Pattern.compile(filterPattern))
                                    .stream()
                                    .map(foundFile -> {
                                        String relatePath = foundFile.getAbsolutePath().replaceFirst(file.getAbsolutePath(), StringUtil.BLANK);
                                        return Paths.get(item, relatePath.substring(0, relatePath.lastIndexOf(StringUtil.DOT))).toString();
                                    })
                                    .collect(Collectors.toList());
                        }
                    } catch (Exception e) {
                        log.warn("(getBasenameHierarchy) Cannot get resource. file = {}, cause = {}", basename, e.getLocalizedMessage());
                    }
                    log.debug("(getBasenameHierarchy) item is file. item = {}", item);
                    return Collections.singleton(item);
                })
                .flatMap(Collection::stream)
                .distinct()
                .toArray(String[]::new);
    }
}
