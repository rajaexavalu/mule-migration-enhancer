package com.exavalu.migration.enhancer.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UniqueStringIdGenerator {
	public static String uniqueStringGenerator(String typeformat) {

		// Get the current date and time up to milliseconds
		LocalDateTime now = LocalDateTime.now();

		// Format the date and time to a string with milliseconds
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(typeformat);

		String uniqueString = now.format(formatter);
		return uniqueString;
	}

}
