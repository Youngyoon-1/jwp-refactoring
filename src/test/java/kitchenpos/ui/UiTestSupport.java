package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.filter.CharacterEncodingFilter;

class UiTestSupport {

    static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER = new CharacterEncodingFilter("UTF-8", true);
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

}
