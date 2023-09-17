package kitchenpos.ui;

import static kitchenpos.ui.UiTestSupport.CHARACTER_ENCODING_FILTER;
import static kitchenpos.ui.UiTestSupport.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class UiTest {

    MockMvc mockMvc;

    MockMvc setupMockMvc(final Object controller) {
        if (mockMvc == null) {
            this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                    .addFilter(CHARACTER_ENCODING_FILTER)
                    .build();
        }
        return this.mockMvc;
    }

    ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
