package dev.kriin.common.spring;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public interface ApiControllerTestSupport {
    ObjectMapper DESERIALIZE_OBJECT_MAPPER = initObjectMapper();

    static ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setAnnotationIntrospectors(objectMapper.getSerializationConfig().getAnnotationIntrospector()
                , new JacksonAnnotationIntrospector() {
                    @Override
                    public JsonProperty.Access findPropertyAccess(Annotated m) {
                        return JsonProperty.Access.AUTO;
                    }
                }
        )
        ;
        return objectMapper;
    }

    default String asBody(Object obj) throws JsonProcessingException {
        return DESERIALIZE_OBJECT_MAPPER.writeValueAsString(obj);
    }
}
