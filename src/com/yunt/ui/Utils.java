package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;

public class Utils extends BaseAct {
	private static ArrayList<Marker> markers = new ArrayList<Marker>();

	public static void searchNearby(final AMap amap, LatLng center) {

		String lon = center.longitude + "";
		String lat = center.latitude + "";

		String url = PathConfig.ADDRESS + "/base/breleasepark/near?lon=" + lon + "&lat=" + lat;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) JSON.parseObject(
						jsondata, new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				HomeAct2.data = data;
				addMarkers(amap, data);

				if (data.size() > 0) {
					HomeAct2.setCarPop("附近有" + data.size() + "个可用车位");
				} else {
					HomeAct2.setCarPop("附近没有可用车位,请移动地图");
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	private static void addMarkers(AMap aMap, ArrayList<HashMap<String, String>> data) {

		if (markers.size() > 0) {
			for (Marker marker : markers) {
				marker.remove();
			}
		}

		for (int j = 0; j < data.size(); j++) {

			// BitmapDescriptor bitmapDescriptor =
			// BitmapDescriptorFactory.fromResource(R.drawable.ub_more_pick);

			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ub_more_pick);
			double Position_x = Double.parseDouble(data.get(j).get("POSITION_X").toString());
			double Position_y = Double.parseDouble(data.get(j).get("POSITION_Y").toString());

			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.setFlat(true);
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(bitmapDescriptor);
			markerOptions.position(new LatLng(Position_y, Position_x));

			markerOptions.snippet(data.get(j).get("CODE") + "," + data.get(j).get("PARK_ADDRESS") + ","
					+ data.get(j).get("PRICE_HOUR") + "," + data.get(j).get("PRICE_MONTH") + ","
					+ data.get(j).get("RELEASE_TYPE") + "," + data.get(j).get("CREATED_BY") + ","
					+ data.get(j).get("PARK_NAME") + "," + data.get(j).get("PARK_PHONE") + ","
					+ data.get(j).get("STAR"));

			Marker marker = aMap.addMarker(markerOptions);
			markers.add(marker);

			HomeAct2.settop();

		}

	}

}
