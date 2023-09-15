package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

public class UiTest {

    MockMvc mockMvc;
    private ObjectMapper objectMapper;

    MockMvc setupMockMvc(final Object controller) {
        if (mockMvc == null) {
            this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                    .addFilter(new CharacterEncodingFilter("UTF-8", true))
                    .build();
        }
        return this.mockMvc;
    }

    ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
        return this.objectMapper;
    }
}
