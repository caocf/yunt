package com.bepo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	/*
	 * @�¼���ת������
	 */
	public static String timesamp2date(String timesamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sd = sdf.format(new Date(Long.parseLong(timesamp)));
		return sd;
	}

}
