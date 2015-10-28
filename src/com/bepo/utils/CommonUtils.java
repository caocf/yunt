package com.bepo.utils;

import java.io.DataOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class CommonUtils {
	private static final String TAG = "CommonUtils";

    //查看是否Root
	public static boolean requestRoot(String pkgCodePath) {
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.e(TAG, "requestRoot, ", e);
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				Log.e(TAG, "requestRoot, ", e);
			}
		}
		return true;
	}

	public static void startBrowserActivity(Activity activity, String url) {
		if (MyTextUtils.isEmpty(url)) {
			LogHelper.w(TAG, "startBrowserActivity, url is null.");
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		CommonUtils.startActivity(activity, intent);
	}

	public static void startActivity(Context context, Intent intent) {
		if (intent == null) {
			LogHelper.w(TAG, "startActivity, intent is null.");
			return;
		}

		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void startActivityForResult(Activity activity, Intent intent,
			int requestCode) {
		if (intent == null) {
			LogHelper.w(TAG, "startActivityForResult, intent is null.");
			return;
		}

		try {
			activity.startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否可以跳转
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static void sendBroadcast(Context context, String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		context.sendBroadcast(intent);
	}

	public static String getVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getVersionName, ", e);
		}

		return null;
	}

	public static int getVersionCode(Context context) throws Exception {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getVersionCode, ", e);
		}

		return 0;
	}

	public static String getUmengChannel(Context context) {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = appInfo.metaData;
			if (bundle != null) {
				String channel = bundle.getString("UMENG_CHANNEL");
				return channel;
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getUmengChannel, ", e);
		}

		return null;
	}

	
	

	public static String getPackageName(Context context, String path) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);
		String pName = "";
		if (info != null) {
			pName = info.packageName;
		}
		return pName;
	}


	public static boolean isPackageExists(Context context, String pkgName) {
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
			if (info != null) {
				PackageInfo pi = pm.getPackageInfo(pkgName, 0);
				if (pi != null) {
					return true;
				}
				return false;
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "isPackageExists, pkgName: " + pkgName
					+ " was not installed.");
		}

		return false;
	}

	
	/**
	 * http://blog.csdn.net/jnhoodlum/article/details/7869571
	 * 
	 * @param urlStr
	 *            可能带有空格的url.
	 * @return 去除了空格的url.
	 */
	public static String getEncodeUrl(String urlStr) {
		if (MyTextUtils.isEmpty(urlStr)) {
			LogHelper.w(TAG, "getEncodeUrl, urlStr is null.");
			return null;
		}

		// 对路径进行编�?然后替换路径中所有空�?编码之后空格变成�?”�?空格的编码表示是�?20�?
		// �?��将所有的�?”替换成�?20”就可以�?
		String url = urlStr.replaceAll(" ", "%20");// 后�?替换前�?
		return url;
	}
	/**
	 * 判断字符是否为中�?	 */
	public static boolean isChinese(char c) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(c + "");
		if (m.find())
			return true;
		return false;
	}
}
