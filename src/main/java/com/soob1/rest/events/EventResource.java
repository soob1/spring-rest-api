package com.soob1.rest.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends RepresentationModel {

	@JsonUnwrapped
	private Event event;

	public EventResource(Event event) {
		this.event = event;
		WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(event.getId());
		add(linkTo(EventController.class).withRel("query-events"));
		add(selfLinkBuilder.withSelfRel());
		add(selfLinkBuilder.withRel("update-event"));
	}

	public Event getEvent() {
		return event;
	}
}
