package dev.kriin.common.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

public interface SpringTestSupport {

    @Slf4j
    @Configuration
    @ComponentScan(value = {"dev.kirin.common"})
    class SpringTestSupportConfig {
        @Autowired
        private WebApplicationContext wac;

        @Bean
        LocalValidatorFactoryBean localValidatorFactoryBean() {
            SpringConstraintValidatorFactory springConstraintValidatorFactory = new SpringConstraintValidatorFactory(wac.getAutowireCapableBeanFactory());
            LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.setConstraintValidatorFactory(springConstraintValidatorFactory);
            validator.setApplicationContext(wac);
            validator.afterPropertiesSet();
            return validator;
        }

        @Bean
        MethodValidationPostProcessor methodValidationPostProcessor() {
            MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
//            methodValidationPostProcessor.setValidatorFactory(localValidatorFactoryBean());
            return methodValidationPostProcessor;
        }
    }
}
