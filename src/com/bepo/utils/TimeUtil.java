package com.bepo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	/*
	 * @事件戳转年月日
	 */
	public static String timesamp2date(String timesamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sd = sdf.format(new Date(Long.parseLong(timesamp)));
		return sd;
	}

	public static String getToday() {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd ");
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.roll(java.util.Calendar.DAY_OF_YEAR, 0);
		return df.format(calendar.getTime());

	}

	public static String getTomorrow() {
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd ");
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		calendar.roll(java.util.Calendar.DAY_OF_YEAR, 1);
		return df.format(calendar.getTime());

	}
}
