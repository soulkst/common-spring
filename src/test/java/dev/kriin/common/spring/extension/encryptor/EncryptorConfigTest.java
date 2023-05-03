package dev.kriin.common.spring.extension.encryptor;

import dev.kirin.common.spring.extension.encryptor.EncryptorConfig;
import dev.kirin.common.spring.extension.encryptor.EncryptorProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@ContextConfiguration(classes = {EncryptorConfig.class, EncryptorProperties.class})
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class EncryptorConfigTest {
    @Test
    void testLoad() {

    }
}
