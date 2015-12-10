/**  
 * Project Name:Android_Car_Example  
 * File Name:LocationTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015��4��3������9:27:45  
 *  
 */

package com.yunt.ui;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * ClassName:LocationTask <br/>
 * Function: �򵥷�װ�˶�λ���󣬿��Խ��е��ζ�λ�Ͷ�ζ�λ��ע�������app������λ����ʱע�����ٶ�λ <br/>
 * Date: 2015��4��3�� ����9:27:45 <br/>
 * 
 */
public class LocationTask implements AMapLocationListener, OnLocationGetListener {

	private LocationManagerProxy mLocationManagerProxy;

	private static LocationTask mLocationTask;

	private Context mContext;

	private OnLocationGetListener mOnLocationGetlisGetListener;

	private RegeocodeTask mRegecodeTask;

	private LocationTask(Context context) {
		mLocationManagerProxy = LocationManagerProxy.getInstance(context);
		mRegecodeTask = new RegeocodeTask(context);
		mRegecodeTask.setOnLocationGetListener(this);
		mContext = context;
	}

	public void setOnLocationGetListener(OnLocationGetListener onGetLocationListener) {
		mOnLocationGetlisGetListener = onGetLocationListener;
	}

	public static LocationTask getInstance(Context context) {
		if (mLocationTask == null) {
			mLocationTask = new LocationTask(context);
		}
		return mLocationTask;
	}

	/**
	 * �������ζ�λ
	 */
	public void startSingleLocate() {
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, this);
	}

	/**
	 * ������ζ�λ
	 */
	public void startLocate() {
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 5 * 1000, 10, this);
	}

	/**
	 * ������λ�����Ը���ζ�λ���ʹ��
	 */
	public void stopLocate() {
		mLocationManagerProxy.removeUpdates(this);

	}

	/**
	 * ���ٶ�λ��Դ
	 */
	public void onDestroy() {
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.destroy();
	}

	@Override
	public void onLocationChanged(Location arg0) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null && amapLocation.getAMapException() != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {

			PositionEntity entity = new PositionEntity();

			// ��γ��
			entity.latitue = amapLocation.getLatitude();
			entity.longitude = amapLocation.getLongitude();

			// ��������
			entity.city = amapLocation.getCity();
			// ���д���
			entity.cityCode = amapLocation.getCityCode();

			// ʡ������
			entity.province = amapLocation.getProvince();

			// ����λ��
			if (!TextUtils.isEmpty(amapLocation.getAddress())) {
				entity.address = amapLocation.getAddress().toString()
						.replace(amapLocation.getProvince().toString(), "");
			}

			mOnLocationGetlisGetListener.onLocationGet(entity);

		}

	}

	@Override
	public void onLocationGet(PositionEntity entity) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onRegecodeGet(PositionEntity entity) {

		// TODO Auto-generated method stub

	}

}
