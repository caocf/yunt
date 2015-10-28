package com.bepo.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public class DeviceUtils {
	private static final String TAG = "DeviceUtils";

	private static final String ISA_ARMV5 = "(v5";
	private static final String ISA_ARMV6 = "armv6";
	private static final String ISA_ARMV7 = "armv7";
	private static final String ISA_NEON = "neon";

	private static String sUDID = "";
	private static String sChannel = "guanwang";
	private static String sVersionName;
	private static int sVersionCode;
	private static String sCpuType;
	private static String sPhoneNum;

	public static int getScreenHeight(Context context) {
		Resources resource = context.getResources();
		Drawable phoneCallIcon = resource
				.getDrawable(android.R.drawable.stat_sys_phone_call);
		int notificationBarHeight = phoneCallIcon.getIntrinsicHeight();
		phoneCallIcon = null;
		int fullScreenHeight = resource.getDisplayMetrics().heightPixels;
		return fullScreenHeight - notificationBarHeight;
	}

	public static int getStatusbarHeight(Activity activity) {
		Resources resource = activity.getResources();
		Drawable phoneCallIcon = resource
				.getDrawable(android.R.drawable.stat_sys_phone_call);
		int notificationBarHeight = phoneCallIcon.getIntrinsicHeight();
		phoneCallIcon = null;
		return notificationBarHeight;
	}

	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static int getScreenWidth(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	public static float getScreenDensity(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.density;
	}


	public static String getUdid() {
		// return "bf1d3a87e826cd8a30311b55f72c08e9";
		LogHelper.v(TAG, "getUdid, udid: " + sUDID);
		return sUDID;
	}

	public static void initChannel(Context context) {
		sChannel = CommonUtils.getUmengChannel(context);
	}

	public static String getChannel() {
		return sChannel;
	}

	public static void initVersionName(Context context) {
		try {
			sVersionName = CommonUtils.getVersionName(context);
		} catch (Exception e) {
			sVersionName = "1.0";
			e.printStackTrace();
		}
	}

	public static String getVersionName() {
		return sVersionName;
	}

	public static void initVersionCode(Context context) {
		try {
			sVersionCode = CommonUtils.getVersionCode(context);
		} catch (Exception e) {
			sVersionCode = 1;
			e.printStackTrace();
		}
	}

	public static int getVersionCode() {
		return sVersionCode;
	}

	private static String encodeDeviceId(String deviceId) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] sources = deviceId.getBytes();
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(sources);
			byte[] md = digest.digest();
			final int size = md.length;
			char[] hexMd = new char[size * 2];
			int index = 0;
			byte element;
			for (int i = 0; i < size; i++) {
				element = md[i];
				hexMd[index++] = hexDigits[element >>> 4 & 0xf];
				hexMd[index++] = hexDigits[element & 0xf];
			}
			return new String(hexMd);
		} catch (Exception e) {
			Log.e(TAG, "encodeDeviceId, ", e);
			return deviceId;
		}
	}

	private static String getCpuInfo() {
		String cpuInfo = "";
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				cpuInfo = cpuInfo + "\n" + line;
			}

			LogHelper.i(TAG, "initCpuType, cpuInfo: " + cpuInfo);
			return cpuInfo;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}

		return null;
	}


	public static String getCpuType() {
		LogHelper.v(TAG, "getCpuType, sCpuType: " + sCpuType);
		return sCpuType;
	}

	/**
	 * 获取屏幕的亮�?
	 * 
	 * @throws SettingNotFoundException
	 */
	public static int getSettingBrightness(Activity activity) {
		try {
			return Settings.System.getInt(activity.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 设置当前屏幕亮度（并非保存）
	 * 
	 * @param progress
	 */
	public static void setScreenBrightness(Activity activity, float brightness) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		// lp.screenBrightness = (float) brightness / 255f; // 0-1
		lp.screenBrightness = brightness;
		activity.getWindow().setAttributes(lp);
	}

	public static float getScreenBrightness(Activity activity) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		return lp.screenBrightness;
	}

	/**
	 * 判断是否�?��了自动亮度调�?
	 */
	public static boolean isAutoBrightness(Activity activity) {
		try {
			return Settings.System.getInt(activity.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * �?��亮度自动调节
	 * 
	 * @param activity
	 */
	public static void startAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	}

	/**
	 * 停止自动亮度调节
	 * 
	 * @param activity
	 */
	public static void stopAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	/**
	 * 硬解全屏方法;
	 * 
	 * @param mp
	 */
	@SuppressWarnings("deprecation")
	public static void sysScreenFull(Activity activity,
			SurfaceView sysSurfaceView, MediaPlayer mp, boolean isFull) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		// Log.i(TAG, "sysScreenFull");
		int mVideoWidth = mp.getVideoWidth();
		int mVideoHeight = mp.getVideoHeight();
		float videoRate = (float) mVideoWidth / (float) mVideoHeight;

		if (mVideoWidth != 0 && mVideoHeight != 0) {

			LayoutParams lp = sysSurfaceView.getLayoutParams();

			float divRate = (float) display.getWidth()
					/ (float) display.getHeight();
			if (videoRate > divRate) {//
				lp.width = display.getWidth();
				if (isFull) {
					lp.height = display.getHeight();
				} else {
					lp.height = lp.width * mVideoHeight / mVideoWidth;
				}
				sysSurfaceView.setLayoutParams(lp);

			} else {//
				lp.height = display.getHeight();
				if (isFull) {
					lp.width = display.getWidth();
				} else {
					lp.width = lp.height * mVideoWidth / mVideoHeight;
				}
				sysSurfaceView.setLayoutParams(lp);
			}
		}
	}

	public static void initPhoneNum(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		sPhoneNum = telephonyManager.getLine1Number();

		LogHelper.i(TAG, "initPhoneNum, sPhoneNum: " + sPhoneNum);
	}

	public static String getPhoneNum() {
		return sPhoneNum;
	}
}
