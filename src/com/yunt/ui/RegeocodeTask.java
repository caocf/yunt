/**  
 * Project Name:Android_Car_Example  
 * File Name:RegeocodeTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015��4��2������6:24:53  
 *  
 */

package com.yunt.ui;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * ClassName:RegeocodeTask <br/>
 * Function: �򵥵ķ�װ���������빦�� <br/>
 * Date: 2015��4��2�� ����6:24:53 <br/>
 * 
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RegeocodeTask implements OnGeocodeSearchListener {
	private static final float SEARCH_RADIUS = 50;
	private OnLocationGetListener mOnLocationGetListener;

	private GeocodeSearch mGeocodeSearch;

	public RegeocodeTask(Context context) {
		mGeocodeSearch = new GeocodeSearch(context);
		mGeocodeSearch.setOnGeocodeSearchListener(this);
	}

	public void search(double latitude, double longitude) {
		RegeocodeQuery regecodeQuery = new RegeocodeQuery(new LatLonPoint(latitude, longitude), SEARCH_RADIUS,
				GeocodeSearch.AMAP);
		mGeocodeSearch.getFromLocationAsyn(regecodeQuery);
	}

	public void setOnLocationGetListener(OnLocationGetListener onLocationGetListener) {
		mOnLocationGetListener = onLocationGetListener;
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult regeocodeReult, int resultCode) {
		if (resultCode == 0) {
			if (regeocodeReult != null && regeocodeReult.getRegeocodeAddress() != null
					&& mOnLocationGetListener != null) {

				String address = regeocodeReult.getRegeocodeAddress().getFormatAddress();
				String city = regeocodeReult.getRegeocodeAddress().getCity();
				String citycode = regeocodeReult.getRegeocodeAddress().getCityCode();

				PositionEntity entity = new PositionEntity();
				entity.address = address;

				entity.address = address.replace(regeocodeReult.getRegeocodeAddress().getProvince(), "");
				entity.city = city;
				entity.cityCode = citycode;
				mOnLocationGetListener.onRegecodeGet(entity);

			}
		}
	}

}
