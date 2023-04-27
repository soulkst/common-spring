package dev.kirin.common.spring.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;

@Slf4j
public abstract class JsonAttributeConverter<T> implements AttributeConverter<T, String> {
    private final ObjectMapper objectMapper;

    private Class<T> targetClass;

    public JsonAttributeConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.targetClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        log.debug("(constructor) targetClass = {}", targetClass.getName());
    }

    @Override
    public T convertToEntityAttribute(String attribute) {
        try  {
            return objectMapper.readValue(attribute, this.targetClass);
        } catch (JsonProcessingException e) {
            throw new ConvertException(e);
        }
    }

    @Override
    public String convertToDatabaseColumn(T dbData) {
        try {
            return objectMapper.writeValueAsString(dbData);
        } catch (JsonProcessingException e) {
            throw new ConvertException(e);
        }
    }

    public static class ConvertException extends RuntimeException {
        public ConvertException(Throwable cause) {
            super(cause);
        }
    }
}
