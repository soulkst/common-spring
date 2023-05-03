package dev.kirin.common.spring.extension.encryptor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter(AccessLevel.MODULE)
@Getter
@ConfigurationProperties(prefix = "app.encryptor")
@Configuration
public class EncryptorProperties {
    private String keyFile = "classpath:default.key";
}
