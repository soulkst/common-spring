package dev.kirin.common.spring.extension.encryptor;

import dev.kirin.common.core.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@Slf4j
@Configuration
public class EncryptorConfig {

    @Bean("jasyptStringEncryptor")
    StringEncryptor stringEncryptor(EncryptorProperties encryptorProperties) throws FileNotFoundException {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(FileUtil.readAsString(ResourceUtils.getFile(encryptorProperties.getKeyFile())));
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        log.info("(stringEncryptor) initialized 'jasyptStringEncryptor'");
        return encryptor;
    }
}
