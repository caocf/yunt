package com.dykj.trajectory;

import java.net.DatagramSocket;
import java.net.SocketException;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.bepo.bean.GPSBean;
import com.bepo.bean.TimeBean;
import com.bepo.core.PathConfig;

import de.greenrobot.event.EventBus;

public class GpsService extends Service implements AMapLocationListener {

	private String TAG = "service";
	private LocationManagerProxy locationManager;
	private OnLocationChangedListener mListener;
	private AMapLocation aLocation;
	public static int recLen = 0;
	WifiManager.MulticastLock lock;
	DatagramSocket socket;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		activate();
		try {
			socket = new DatagramSocket(8989);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private void initTime() {
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				recLen++;
				TimeBean tb = new TimeBean();
				tb.setTime(recLen);
				if (PathConfig.isRuning) {
					EventBus.getDefault().post(tb);
				}
				handler.postDelayed(this, 1000);
			}
		};

		handler.postDelayed(runnable, 1000);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);

	}

	public void activate() {

		if (locationManager == null) {
			locationManager = LocationManagerProxy.getInstance(this);
			locationManager.requestLocationData(LocationManagerProxy.GPS_PROVIDER, 2000, 10, this);
//			locationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
			
		}

	}

	@Override
	public void onLocationChanged(Location arg0) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {

		if (aLocation != null && aLocation.getAMapException().getErrorCode() == 0) {
			// 获取位置信息
			Double geoLat = aLocation.getLatitude();
			Double geoLng = aLocation.getLongitude();
			String location = geoLat.toString() + "," + geoLng.toString();

			GPSBean mGPSBean = new GPSBean();
			mGPSBean.setX(geoLat + "");
			mGPSBean.setY(geoLng + "");
			EventBus.getDefault().post(mGPSBean);
			String message = PathConfig.userCode + "," + geoLng + "," + geoLat + "," + "1" + "," + "1";
			try {
				sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e(TAG, message);

		}

	}

	public void sendMessage(final String message) throws Exception {

		Runnable task = new Runnable() {
			@Override
			public void run() {
				UDPClient.send(message);

			}
		};
		new Thread(task).start();
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

}
