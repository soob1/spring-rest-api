package com.soob1.rest.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

	public void validate(EventDto eventDto, Errors errors) {
		if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
			errors.rejectValue("basePrice", "WrongValue", "BasePrice is wrong");
			errors.rejectValue("maxPrice", "WrongValue", "MaxPrice is wrong");
		}

		LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
		if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
		endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
		endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())) {
			errors.rejectValue("endEventDateTime", "WrongValue", "endEventDateTime is wrong");
		}

		// TODO BeginEventDateTime
		// TODO CloseEnrollmentDateTime
	}
}
