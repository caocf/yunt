package com.bepo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.update.UpdateManager;
import com.yunt.ui.HomeAct;

public class SplashAct extends BaseAct {

	private static final String uploadUrl = PathConfig.ADDRESS + "gsm/upload/android/version.xml";
	private UpdateManager manager = new UpdateManager(uploadUrl, this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		start();
	}

	private void start() {
		new Thread() {
			public void run() {
				try {
					sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Looper.prepare();
				// manager.checkUpdate();
				Intent mIntent = new Intent(SplashAct.this, HomeAct.class);
				// Intent mIntent = new Intent(SplashAct.this,
				// LoginActivity.class);
				SplashAct.this.startActivity(mIntent);
				finish();
			}
		}.start();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.hold);
	}

	// private void initChartData() {
	// String url = PathConfig.ADDRESS +
	// "gsm/charts/bpersoncharts/personCharts";
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("gridcode", PathConfig.girdCode);
	// Request<JSONObject> request = new VolleyCommonPost(url, new
	// Response.Listener<JSONObject>() {
	// @Override
	// public void onResponse(JSONObject response) {
	//
	// String jsondata = response.toString();
	// Map<String, String> message = JSON.parseObject(jsondata, new
	// TypeReference<Map<String, String>>() {
	// });
	//
	// String xdataStr = message.get("categories");
	// if (xdataStr.indexOf(",") >= 0) {
	// // xdata = (ArrayList<String>)
	// // Arrays.asList(xdataStr.split(","));
	//
	// String ss[] = xdataStr.split(",");
	// for (String m : ss) {
	// PathConfig.xdata.add(m);
	// }
	//
	// } else {
	// PathConfig.xdata.add(xdataStr);
	// }
	//
	// String ydataStr = message.get("seriesdata");
	// if (ydataStr.indexOf(",") >= 0) {
	// // ydata = (ArrayList<String>)
	// // Arrays.asList(ydataStr.split(","));
	//
	// String ss[] = ydataStr.split(",");
	// for (String m : ss) {
	// PathConfig.ydata.add(m);
	// }
	//
	// } else {
	// PathConfig.ydata.add(ydataStr);
	// }
	// start();
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// }
	// }, params);
	//
	// ApplicationController.getInstance().addToRequestQueue(request);
	// }

}
