/**  
 * Project Name:Android_Car_Example  
 * File Name:RouteTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015��4��3������2:38:10  
 *  
 */

package com.yunt.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;

/**
 * ClassName:RouteTask <br/>
 * Function: ��װ�ļݳ�·���滮 <br/>
 * Date: 2015��4��3�� ����2:38:10 <br/>
 * 
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RouteTask implements OnRouteSearchListener {

	private static RouteTask mRouteTask;

	private RouteSearch mRouteSearch;

	private PositionEntity mFromPoint;

	private PositionEntity mToPoint;

	private List<OnRouteCalculateListener> mListeners = new ArrayList<RouteTask.OnRouteCalculateListener>();

	public interface OnRouteCalculateListener {
		public void onRouteCalculate(float cost, float distance, int duration);

	}

	public static RouteTask getInstance(Context context) {
		if (mRouteTask == null) {
			mRouteTask = new RouteTask(context);
		}
		return mRouteTask;
	}

	public PositionEntity getStartPoint() {
		return mFromPoint;
	}

	public void setStartPoint(PositionEntity fromPoint) {
		mFromPoint = fromPoint;
	}

	public PositionEntity getEndPoint() {
		return mToPoint;
	}

	public void setEndPoint(PositionEntity toPoint) {
		mToPoint = toPoint;
	}

	private RouteTask(Context context) {
		mRouteSearch = new RouteSearch(context);
		mRouteSearch.setRouteSearchListener(this);
	}

	public void search() {
		if (mFromPoint == null || mToPoint == null) {
			return;
		}

		FromAndTo fromAndTo = new FromAndTo(new LatLonPoint(mFromPoint.latitue,
				mFromPoint.longitude), new LatLonPoint(mToPoint.latitue,
				mToPoint.longitude));
		DriveRouteQuery driveRouteQuery = new DriveRouteQuery(fromAndTo,
				RouteSearch.DrivingDefault, null, null, "");

		mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
	}

	public void search(PositionEntity fromPoint, PositionEntity toPoint) {

		mFromPoint = fromPoint;
		mToPoint = toPoint;
		search();

	}

	public void addRouteCalculateListener(OnRouteCalculateListener listener) {
		synchronized (this) {
			if (mListeners.contains(listener))
				return;
			mListeners.add(listener);
		}
	}

	public void removeRouteCalculateListener(OnRouteCalculateListener listener) {
		synchronized (this) {
			mListeners.add(listener);
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult driveRouteResult,
			int resultCode) {
		if (resultCode == 0 && driveRouteResult != null) {
			synchronized (this) {
				for (OnRouteCalculateListener listener : mListeners) {
					List<DrivePath> drivepaths = driveRouteResult.getPaths();
					float distance = 0;
					int duration = 0;
					if (drivepaths.size() > 0) {
						DrivePath drivepath = drivepaths.get(0);

						distance = drivepath.getDistance() / 1000;

						duration = (int) (drivepath.getDuration() / 60);
					}

					float cost = driveRouteResult.getTaxiCost();

					listener.onRouteCalculate(cost, distance, duration);
				}

			}
		}
		// TODO ���Ը���app��������Բ�ѯ�������������Ӧ����ʾ�����߼�����
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

		// TODO Auto-generated method stub

	}

}
