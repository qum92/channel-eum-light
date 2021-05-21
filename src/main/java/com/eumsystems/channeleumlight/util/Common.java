package com.eumsystems.channeleumlight.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class Common {

	public static String getCurrentTime(String format) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat(format);
		String dateString = df.format(now);
		return dateString;
	}
}
