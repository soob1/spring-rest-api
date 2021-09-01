package com.soob1.rest.index;

import com.soob1.rest.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexControllerTest extends BaseControllerTest {

	@Test
	@DisplayName("index 조회")
	public void index() throws Exception {
		mockMvc.perform(get("/api/"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("_links.events").exists())
		;
	}
}
