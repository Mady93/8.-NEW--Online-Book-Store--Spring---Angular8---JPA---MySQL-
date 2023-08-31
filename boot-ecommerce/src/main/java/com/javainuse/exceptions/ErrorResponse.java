package com.javainuse.exceptions;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> {

	private String timestamp;
	private Integer status;
	private T errors;
	private String message;
	private String[] trace;
	private String path;

	public ErrorResponse(Integer status, T errors, String message, String[] trace, String path) {

		this.timestamp = ZonedDateTime.now(ZoneId.of("Europe/Rome"))
				.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS z"));

		this.status = status;
		this.errors = errors;
		this.message = message;
		this.trace = trace;
		this.path = path;
	}

	public ErrorResponse(String timestamp, Integer status, T errors, String message, String[] trace, String path) {
		this.timestamp = timestamp;
		this.status = status;
		this.errors = errors;
		this.message = message;
		this.trace = trace;
		this.path = path;
	}
}
