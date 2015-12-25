package com.bepo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bepo.core.PathConfig;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

public class MyTextUtils {
	private static final String TAG = "MyTextUtils";

	private static final int FIRST_MAX_LENGTH = 22;
	private static final int SENCOND_MAX_LENGTH = 18;
	private static final int NAME_PREV_MAX_LENGTH = 18;
	private static final int NAME_NEXT_MAX_LENGTH = 0;

	public static String getUTFstr(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;

	};

	public static Boolean isZimu(String s) {
		String reg = "[a-zA-Z]";
		return s.matches(reg);
	}

	public static void showToast(final String toast, final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}).start();
	}

	// a integer to xx:xx:xx
	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	public static android.widget.LinearLayout.LayoutParams getParams(Activity mActivity, double d, int space) {
		int w = (int) (d * 100);
		int scerrnwith = DeviceUtils.getScreenWidth(mActivity) - dip2px(mActivity, space);
		int width = (int) (scerrnwith * w / 10000.00);
		LogHelper.i(TAG, "getParams,width = " + width);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, dip2px(mActivity, 16));
		return params;
	}

	public static LayoutParams getFrameParams(Activity mActivity, double d, int space) {
		int w = (int) (d * 100);
		float scerrnwith = DeviceUtils.getScreenWidth(mActivity) - dip2px(mActivity, space);
		int width = (int) (scerrnwith * (w / 10000.00));
		LogHelper.i(TAG, "getParams,width = " + width);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, dip2px(mActivity, 16));
		return params;
	}

	public static boolean isChinese(char c) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(c + "");
		if (m.find())
			return true;
		return false;
	}

	public static String noSpace(String content) {
		return content.replaceAll("\\s*", "");
	}

	public static boolean isEmpty(String content) {
		if (content == null || "".equals(content.trim())) {
			return true;
		}
		return false;
	}

	public static String isZeroToString(String content) {
		if (content == null || "".equals(content) || "0".equals(content) || "0.0".equals(content)) {

			return "";
		}
		return content;

	}

	public static boolean isArrayEmpty(String[] content) {
		if (content == null || content.length == 0) {
			return true;
		}
		return false;
	}

	public static String getOnelineFormatName(String str) {
		if (isEmpty(str)) {
			return "";
		}

		return getFormatSecLine(str, 25, 11, 11);
	}

	public static String getTwolineFormatName(String str) {
		char[] chars = str.toCharArray();

		String firstLine = "";
		String secondLine = "";
		int firstLength = 0;
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = String.valueOf(chars[i]).getBytes();
			// LogHelper.w(TAG, "getFormatName, chars[" + i + "]" + chars[i] +
			// " ,length: " + bytes.length);

			// firstLength = firstLength + bytes.length;
			if (isChinese(chars[i])) {
				firstLength = firstLength + 2;
			} else {
				firstLength = firstLength + bytes.length;
			}

			if (firstLength <= FIRST_MAX_LENGTH) {
				firstLine = firstLine + chars[i];
			} else {
				secondLine = secondLine + chars[i];
			}
		}

		secondLine = getFormatSecLine(secondLine, SENCOND_MAX_LENGTH, NAME_PREV_MAX_LENGTH, NAME_NEXT_MAX_LENGTH);
		String name = firstLine + "\n" + secondLine;

		return name;
	}

	private static String getFormatSecLine(String secondLine, int maxLength, int prevLength, int lastLength) {
		String prevStr = secondLine;
		String lastStr = secondLine;
		char[] chars = secondLine.toCharArray();

		int sencondLength = 0;
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = String.valueOf(chars[i]).getBytes();

			if (isChinese(chars[i])) {
				sencondLength = sencondLength + 2;
			} else {
				sencondLength = sencondLength + bytes.length;
			}
		}

		if (sencondLength <= maxLength) {
			return secondLine;
		} else {
			return getPrevChars(prevStr, prevLength) + "..." + getLastChars(lastStr, lastLength);
		}
	}

	private static String getPrevChars(String secondLine, int prevCount) {
		char[] chars = secondLine.toCharArray();

		int preLength = 0;
		String prevStr = "";
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = String.valueOf(chars[i]).getBytes();
			// preLength = preLength + bytes.length;
			if (isChinese(chars[i])) {
				preLength = preLength + 2;
			} else {
				preLength = preLength + bytes.length;
			}

			if (preLength <= prevCount) {
				prevStr = prevStr + chars[i];
			} else {
				break;
			}
		}

		return prevStr;
	}

	private static String getLastChars(String secondLine, int lastCount) {
		char[] chars = secondLine.toCharArray();

		int lastLength = 0;
		String lastStr = "";
		for (int i = chars.length - 1; i >= 0; i--) {
			byte[] bytes = String.valueOf(chars[i]).getBytes();
			// lastLength = lastLength + bytes.length;
			if (isChinese(chars[i])) {
				lastLength = lastLength + 2;
			} else {
				lastLength = lastLength + bytes.length;
			}

			if (lastLength <= lastCount) {
				lastStr = chars[i] + lastStr;
			} else {
				break;
			}
		}

		return lastStr;
	}

	// 服务端以s为单位，android端以ms为单�?
	public static String getDateString(long position) {
		Date date = new Date(position * 1000);
		// yyyy/MM/dd
		SimpleDateFormat fmPlayTime = new SimpleDateFormat("dd/MM/yyyy");
		fmPlayTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = fmPlayTime.format(date);
		return dateStr;
	}

	public static String getTimeString(long position) {
		Date date = new Date(position);
		// yyyy/MM/dd
		SimpleDateFormat fmPlayTime = new SimpleDateFormat("dd/MM/yyyy");
		fmPlayTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = fmPlayTime.format(date);
		return dateStr;
	}

	// 服务端以s为单位，android端以ms为单�?
	public static String getTimeString(String position) {
		try {
			long time = Long.parseLong(position);
			Date date = new Date(time * 1000);
			// yyyy/MM/dd
			SimpleDateFormat fmPlayTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			fmPlayTime.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			String dateStr = fmPlayTime.format(date);
			return dateStr;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	// 服务端以s为单位，android端以ms为单�?
	public static String getCurrentTimeString() {
		try {
			Date date = new Date();
			// yyyy/MM/dd
			SimpleDateFormat fmPlayTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			fmPlayTime.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			String dateStr = fmPlayTime.format(date);
			return dateStr;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	// 服务端以s为单位，android端以ms为单�?
	public static String getDateStringShare(long position) {
		Date date = new Date(position * 1000);
		SimpleDateFormat fmPlayTime = new SimpleDateFormat("yyyyMMdd");
		fmPlayTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateStr = fmPlayTime.format(date);
		return dateStr;
	}

	/**
	 * 123000 --> 123 --> 2:03
	 * 
	 * @param position
	 */
	public static String getStringTime(long position) {
		SimpleDateFormat fmPlayTime;
		if (position <= 0) {
			return "00:00";
		}

		long lCurrentPosition = position / 1000;
		long lHours = lCurrentPosition / 3600;

		if (lHours > 0)
			fmPlayTime = new SimpleDateFormat("HH:mm:ss");
		else
			fmPlayTime = new SimpleDateFormat("mm:ss");

		fmPlayTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		return fmPlayTime.format(position);
	}

	public static String getStringTime(String position) {
		long pos = Long.parseLong(position);
		return getStringTime(pos);
	}

	/**
	 * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * �?6进制转换为字符串
	 * 
	 * @param hexStr
	 * @return
	 */
	public static String parseHexStr2Byte(String hexStr) {
		try {
			if (hexStr.length() < 1)
				return null;
			byte[] result = new byte[hexStr.length() / 2];
			for (int i = 0; i < hexStr.length() / 2; i++) {
				int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
				int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
				result[i] = (byte) (high * 16 + low);
			}
			return new String(result, "utf-8");
		} catch (Exception e) {
			Log.e(TAG, "parseHexStr2Byte, ", e);
			return hexStr;
		}
	}

	public static ArrayList<String> toArrayList(String[] strings) {
		ArrayList<String> list = new ArrayList<String>();
		if (strings == null) {
			return list;
		}
		for (int i = 0; i < strings.length; i++) {
			list.add(strings[i]);
		}
		return list;
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static String urlPlusFoot(String url) {

		return url + "?clientkey=" + PathConfig.clientkey;
	}

	public static String urlPlusAndFoot(String url) {

		return url + "&clientkey=" + PathConfig.clientkey;
	}

}
