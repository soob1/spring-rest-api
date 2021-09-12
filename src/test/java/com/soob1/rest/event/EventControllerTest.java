package com.soob1.rest.event;

import com.soob1.rest.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {

	@Autowired
	EventRepository eventRepository;

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
				.andExpect(jsonPath("_links.update-event").exists())
				.andDo(document("create-event",
							links(
									linkWithRel("self").description("link to self"),
									linkWithRel("query-events").description("link to query events"),
									linkWithRel("update-event").description("link to update an existing event"),
									linkWithRel("profile").description("link to profile")
							),
							requestHeaders(
									headerWithName(HttpHeaders.ACCEPT).description("Accept header"),
									headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-Type header")
							),
							requestFields(
									fieldWithPath("name").description("Name of new event"),
									fieldWithPath("description").description("Description of new event"),
									fieldWithPath("beginEnrollmentDateTime").description("Date time of begin of new event"),
									fieldWithPath("closeEnrollmentDateTime").description("Date time of close of new event"),
									fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
									fieldWithPath("endEventDateTime").description("Date time of end of new event"),
									fieldWithPath("location").description("Location of new event"),
									fieldWithPath("basePrice").description("Base price of new event"),
									fieldWithPath("maxPrice").description("Max price of new event"),
									fieldWithPath("limitOfEnrollment").description("Limit of enrollment")
							),
							responseHeaders(
									headerWithName(HttpHeaders.LOCATION).description("Location header"),
									headerWithName(HttpHeaders.CONTENT_TYPE).description("Content-Type header")
							),
							relaxedResponseFields(	// relaxed prefix: 응답의 일부 필드만 기술
									fieldWithPath("id").description("Id of new event"),
									fieldWithPath("name").description("Name of new event"),
									fieldWithPath("description").description("Description of new event"),
									fieldWithPath("beginEnrollmentDateTime").description("Date time of begin of new event"),
									fieldWithPath("closeEnrollmentDateTime").description("Date time of close of new event"),
									fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
									fieldWithPath("endEventDateTime").description("Date time of end of new event"),
									fieldWithPath("location").description("Location of new event"),
									fieldWithPath("basePrice").description("Base price of new event"),
									fieldWithPath("maxPrice").description("Max price of new event"),
									fieldWithPath("limitOfEnrollment").description("Limit of enrollment"),
									fieldWithPath("free").description("It tells if this event is free or not"),
									fieldWithPath("offline").description("It tells if this event is offline event or not"),
									fieldWithPath("eventStatus").description("Event status")
							)
						)
				);
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
				.andExpect(jsonPath("errors[0].objectName").exists())
				.andExpect(jsonPath("errors[0].defaultMessage").exists())
				.andExpect(jsonPath("errors[0].code").exists())
				.andExpect(jsonPath("_links.index").exists());
	}

	@Test
	@DisplayName("이벤트 목록 조회")
	public void queryEvents() throws Exception {
		// given
		IntStream.range(0, 30).forEach(this::generateEvent);

		// when & then
		mockMvc.perform(get("/api/events")
				.param("page", "1")
				.param("size", "10")
				.param("sort", "name,DESC"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("page").exists())
				.andExpect(jsonPath("_links.self").exists());
	}

	@Test
	@DisplayName("이벤트 조회")
	public void getEvent() throws Exception {
		// given
		Event event = this.generateEvent(100);

		// when & then
		mockMvc.perform(get("/api/events/{id}", event.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
		;
	}

	@Test
	@DisplayName("존재하지 않는 이벤트 조회")
	public void getEvent404() throws Exception {
		mockMvc.perform(get("/api/events/{id}", 1234))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("이벤트 수정")
	public void updateEvent() throws Exception {
		// given
		Event event = this.generateEvent(1);

		EventDto eventDto = modelMapper.map(event, EventDto.class);
		String eventName = "Updated Event";
		eventDto.setName(eventName);

		// when & then
		mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").value(eventName))
				.andExpect(jsonPath("_links.self").exists());
	}

	@Test
	@DisplayName("입력값이 비어있는 경우 이벤트 수정 실패")
	public void updateEvent_Bad_Request_Empty_Input() throws Exception {
		// given
		Event event = this.generateEvent(1);

		EventDto eventDto = new EventDto();

		// when & then
		mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("입력값이 잘못된 경우 이벤트 수정 실패")
	public void updateEvent_Bad_Request_Wrong_Input() throws Exception {
		// given
		Event event = this.generateEvent(1);

		EventDto eventDto = modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);

		// when & then
		mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("존재하지 않는 이벤트 수정 실패")
	public void updateEvent404() throws Exception {
		// given
		Event event = this.generateEvent(1);
		EventDto eventDto = modelMapper.map(event, EventDto.class);

		mockMvc.perform(put("/api/events/{id}", 123123)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("이벤트 수정 권한 없음")
	public void updateEvent_Permission_Denied() {

	}

	// 이벤트 생성
	private Event generateEvent(int index) {
		Event event = Event.builder()
				.name("event " + index)
				.description("test event")
				.beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 21, 16, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 22, 16, 0))
				.beginEventDateTime(LocalDateTime.of(2021, 8, 23, 16, 0))
				.endEventDateTime(LocalDateTime.of(2021, 8, 24, 16, 0))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역")
				.free(false)
				.offline(true)
				.eventStatus(EventStatus.DRAFT)
				.build();
		eventRepository.save(event);

		return event;
	}
}
