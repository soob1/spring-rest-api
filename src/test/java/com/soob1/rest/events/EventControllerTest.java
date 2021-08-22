package com.soob1.rest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("이벤트 생성")
	public void createEvent() throws Exception {

		EventDto eventDto = EventDto.builder()
				.name("spring")
				.description("rest api development with spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 21, 16, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 22, 16, 0))
				.beginEventDateTime(LocalDateTime.of(2021, 8, 23, 16, 0))
				.endEventDateTime(LocalDateTime.of(2021, 8, 24, 16, 0))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역")
				.build();

		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
				.andExpect(jsonPath("free").value(false))
				.andExpect(jsonPath("offline").value(true))
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.query-events").exists())
				.andExpect(jsonPath("_links.update-event").exists());
	}

	@Test
	@DisplayName("이벤트 생성 시 입력값 제한")
	public void createEvent_Bad_Request() throws Exception {

		Event event = Event.builder()
				.id(2)
				.name("spring")
				.description("rest api development with spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 21, 16, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 22, 16, 0))
				.beginEventDateTime(LocalDateTime.of(2021, 8, 23, 16, 0))
				.endEventDateTime(LocalDateTime.of(2021, 8, 24, 16, 0))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역")
				.free(true)
				.eventStatus(EventStatus.PUBLISHED)
				.build();

		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("이벤트 생성 시 필수값 누락 에러 처리")
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDto eventDto = EventDto.builder().build();

		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("이벤트 생성 시 유효하지 않은 입력값 에러 처리")
	public void createEvent_Bad_Request_Wrong_Input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("spring")
				.description("rest api development with spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 24, 16, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 23, 16, 0))
				.beginEventDateTime(LocalDateTime.of(2021, 8, 22, 16, 0))
				.endEventDateTime(LocalDateTime.of(2021, 8, 21, 16, 0))
				.basePrice(10000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역")
				.build();

		mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists())
				.andExpect(jsonPath("$[0].code").exists());
	}
}
