package com.sbr.userapi.service.time;

import java.util.Calendar;

import org.springframework.stereotype.Service;

@Service
public class TimeService {
	public long getCurrentDateTimeTimestamp() {
		return Calendar.getInstance().getTimeInMillis();
	}
}