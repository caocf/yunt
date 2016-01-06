package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.CarPortListAdapter;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.AMapUtil;

public class NearCarPortListActivity extends BaseAct implements TextWatcher, OnClickListener,
		OnGeocodeSearchListener {

	CarPortListAdapter mCarPortListAdapter;
	private GeocodeSearch geocoderSearch;

	private ListView listView;
	private AutoCompleteTextView searchText;

	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private String keyword;
	private String city;
	private LatLng mLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carport_list);
		getTopBar("附近车位列表");

		if (HomeAct2.data.size() > 0) {
			data = HomeAct2.data;
		}
		
		city = HomeAct2.city;
		initView();

	}

	private void initView() {

		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);

		searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
		searchText.addTextChangedListener(this);

		listView = (ListView) this.findViewById(R.id.listView);
		mCarPortListAdapter = new CarPortListAdapter(data, listView, this);
		listView.setAdapter(mCarPortListAdapter);

	}

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);

				mLatLng = new LatLng(address.getLatLonPoint().getLatitude(), address.getLatLonPoint()
						.getLongitude());

				getData(mLatLng);

			} else {
			}
		}
	}

	private void getData(LatLng center) {
		String lon = center.longitude + "";
		String lat = center.latitude + "";

		String url = PathConfig.ADDRESS + "/base/breleasepark/near?lon=" + lon + "&lat=" + lat;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				data.clear();
				data = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});
				if (data.size() > 0) {

					mCarPortListAdapter.setData(data);
					listView.setAdapter(mCarPortListAdapter);
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {

	}

	@Override
	public void onClick(View arg0) {

	}

	@Override
	public void afterTextChanged(Editable arg0) {
		keyword = AMapUtil.checkEditText(searchText);
		getLatlon(keyword);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
		Inputtips inputTips = new Inputtips(NearCarPortListActivity.this, new InputtipsListener() {

			@Override
			public void onGetInputtips(List<Tip> tipList, int rCode) {
				if (rCode == 0) {
					List<String> listString = new ArrayList<String>();
					for (int i = 0; i < tipList.size(); i++) {
						listString.add(tipList.get(i).getName());
					}
					ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.route_inputs, listString);
					searchText.setAdapter(aAdapter);
					aAdapter.notifyDataSetChanged();
				}
			}
		});
		try {
			inputTips.requestInputtips(newText, "");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

		} catch (AMapException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {
		GeocodeQuery query = new GeocodeQuery(name, city);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

}
