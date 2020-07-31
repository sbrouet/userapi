package com.sbr.userapi.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDetails {
	private long timestamp;
	private String message;
	private String requestDetails;
}