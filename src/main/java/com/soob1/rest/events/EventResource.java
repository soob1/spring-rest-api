package com.soob1.rest.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class EventResource extends RepresentationModel {

	@JsonUnwrapped
	private Event event;

	public EventResource(Event event, WebMvcLinkBuilder selfLinkBuilder) {
		this.event = event;
		add(selfLinkBuilder.withSelfRel());
	}

	public Event getEvent() {
		return event;
	}
}
