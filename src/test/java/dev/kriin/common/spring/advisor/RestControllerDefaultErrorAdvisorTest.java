package dev.kriin.common.spring.advisor;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import dev.kirin.common.spring.constraint.CreateValidGroup;
import dev.kirin.common.spring.constraint.annotation.EnhancedNotEmpty;
import dev.kirin.common.spring.model.vo.ApiErrorVo;
import dev.kriin.common.spring.ApiControllerTestSupport;
import dev.kriin.common.spring.SpringTestSupport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(properties = {"spring.profiles.active=test", "logging.level.dev.kirin.common.spring.advisor=info"})
@ContextConfiguration(classes = {SpringTestSupport.SpringTestSupportConfig.class, RestControllerDefaultErrorAdvisorTest.TestApiController.class})
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
class RestControllerDefaultErrorAdvisorTest implements ApiControllerTestSupport {
    private static final String BASE_URI = "/test-api";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("[ConstraintViolationException] Invalid requested 'null' string value to pathVariable")
    void testConstraintViolationException_requestedNullValue() throws Exception {
        TestApiController.TestVo requestVo = new TestApiController.TestVo();
        requestVo.setName("testname");
        String requestURI = BASE_URI + "/null";
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.put(requestURI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asBody(requestVo))
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals("javax.validation.ConstraintViolationException", errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNotNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[ConstraintViolationException] Invalid requested 'undefined' value to pathVariable")
    void testConstraintViolationException_requestedUndefinedValue() throws Exception {
        TestApiController.TestVo requestVo = new TestApiController.TestVo();
        requestVo.setName("testname");
        String requestURI = BASE_URI + "/undefined";
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(ConstraintViolationException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNotNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[BindException] Defined @NotEmpty")
    void testBindException_definedNotEmpty() throws Exception {
        TestApiController.TestVo requestVo = new TestApiController.TestVo();
        String requestURI = BASE_URI + "/test-id";
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(MethodArgumentNotValidException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNotNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[MethodArgumentTypeMismatchException] PathVariable defined integer, But requested String")
    void testMethodArgumentTypeMismatchException_pathVariablesDefinedIntegerButRequestString() throws Exception {
        TestApiController.TestVo requestVo = new TestApiController.TestVo();
        String requestURI = BASE_URI + "/test-id/integer";
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.put(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(MethodArgumentTypeMismatchException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[MethodArgumentTypeMismatchException] RequestParam defined integer, but requested String")
    void testMethodArgumentTypeMismatchException_definedIntegerButRequestString() throws Exception {
        TestApiController.TestVo requestVo = new TestApiController.TestVo();
        String requestURI = BASE_URI;
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("required", "abc")
                                .content(asBody(requestVo))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(MethodArgumentTypeMismatchException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[MissingServletRequestParameterException] Missing Required parameter")
    void testMissingServletRequestParameterException_missingRequiredParameter() throws Exception {
        TestApiController.TestVo requestVo = new TestApiController.TestVo();
        String requestURI = BASE_URI;
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(requestVo))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(MissingServletRequestParameterException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[HttpMessageNotReadableException] Invalid JSON format requested")
    void testHttpMessageNotReadableException_invalidJsonFormat() throws Exception {
        String requestURI = BASE_URI;
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{name:asdf}")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(HttpMessageNotReadableException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertNotNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[HttpMessageNotReadableException] JSON field type miss match")
    void testHttpMessageNotReadableException_jsonFieldMissMatch() throws Exception {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("name", "test");
        bodyMap.put("testNumber", "aaaa");

        String requestURI = BASE_URI;
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asBody(bodyMap))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(InvalidFormatException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNotNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[HttpMediaTypeNotSupportedException] Invalid content type")
    void testHttpMediaTypeNotSupportedException_invalidContentType() throws Exception {
        TestApiController.TestVo requestVo = new TestApiController.TestVo();
        String requestURI = BASE_URI;
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(requestURI)
                                .contentType(MediaType.TEXT_HTML)
                                .content(asBody(requestVo))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(HttpMediaTypeNotSupportedException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertNotNull(errorVo.getMore());
    }

    @Test
    @DisplayName("[HttpRequestMethodNotSupportedException] Not found method")
    void testHttpRequestMethodNotSupportedException_notFound() throws Exception {
        String requestURI = BASE_URI + "/unknown";
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(HttpRequestMethodNotSupportedException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertTrue(StringUtils.hasText((String) errorVo.getMore()));
    }

    @Test
    @DisplayName("[UnknownException] Unknown(or Undefined) Error")
    void testUnknown() throws Exception {
        String requestURI = BASE_URI + "/unknown";
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get(requestURI)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasText(responseBody));
        ApiErrorVo errorVo = DESERIALIZE_OBJECT_MAPPER.readValue(responseBody, ApiErrorVo.class);
        Assertions.assertNotNull(errorVo);
        Assertions.assertEquals(IllegalArgumentException.class.getName(), errorVo.getType());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorVo.getStatus());
        Assertions.assertEquals(requestURI, errorVo.getInstance());
        Assertions.assertTrue(StringUtils.hasText(errorVo.getTitle()));
        Assertions.assertTrue(StringUtils.hasText(errorVo.getDetail()));
        Assertions.assertTrue(errorVo.getMore() instanceof String);
        Assertions.assertTrue(StringUtils.hasText((String) errorVo.getMore()));
    }

    @RestController
    @RequestMapping(value = BASE_URI)
    @Validated
    public static class TestApiController {

        @PostConstruct
        void postConstruct() {
            log.info("Enabled test controller");
        }

        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public void post(@Validated(CreateValidGroup.class) @RequestBody TestVo body) {

        }

        @PutMapping(value = "/{id}")
        public void put(@PathVariable("id") @EnhancedNotEmpty String id
                , @Validated(CreateValidGroup.class) @RequestBody TestVo body) {
        }

        @PutMapping(value = "/{id}/integer")
        public void putByInt(@PathVariable("id") @EnhancedNotEmpty Integer id
                , @Validated(CreateValidGroup.class) @RequestBody TestVo body) {

        }

        @GetMapping
        public void getRequiredParam(@RequestParam("required") boolean required) {

        }

        @GetMapping(value = "/unknown")
        public void throwUnknown() {
            throw new IllegalArgumentException("Illegal");
        }

        @Data
        @NoArgsConstructor
        @Builder
        @AllArgsConstructor
        public static class TestVo {
            @NotEmpty(groups = {CreateValidGroup.class})
            private String name;
            private Integer testNumber;
        }
    }
}
