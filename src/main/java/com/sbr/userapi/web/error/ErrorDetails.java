package com.sbr.userapi.web.error;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDetails {
	private long timestamp;
	private String message;
	private List<String> details;
	private String requestDetails;
}