package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.filter.CharacterEncodingFilter;

class UiTestSupport {

    static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER = new CharacterEncodingFilter("UTF-8", true);
    static final ObjectMapper OBJECT_MAPPER = initObjectMapper();

    private static ObjectMapper initObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
