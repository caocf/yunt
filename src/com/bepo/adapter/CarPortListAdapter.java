package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;
import com.yunt.ui.ParkDetailAct;

@SuppressLint("UseSparseArrays")
@SuppressWarnings("rawtypes")
public class CarPortListAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;
	private String ownerName;
	private String ownerPhone;

	@SuppressWarnings("unchecked")
	public CarPortListAdapter(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public CarPortListAdapter(ArrayList<HashMap<String, String>> data, ListView listView, Context context) {
		super(data, context);
		this.listView = listView;
		this.context = context;
		this.data = (ArrayList<HashMap<String, String>>) data;
		inflater = ((Activity) context).getLayoutInflater();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) context;

		View rowView = convertView;
		final ViewHolder viewCache;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.carport_items, null);
			viewCache = new ViewHolder();

			viewCache.tvHourPrice = (TextView) rowView.findViewById(R.id.tvHourPrice);
			viewCache.tvMonthPirce = (TextView) rowView.findViewById(R.id.tvMonthPirce);
			viewCache.tvAddress = (TextView) rowView.findViewById(R.id.tvAddress);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("PRICE_HOUR"))) {
			viewCache.tvHourPrice.setText(data.get(position).get("PRICE_HOUR").toString() + "元/时");
		}

		if (!(null == data.get(position).get("PRICE_MONTH"))) {
			viewCache.tvMonthPirce.setText(data.get(position).get("PRICE_MONTH").toString() + "元/月");
		}

		if (!(null == data.get(position).get("PARK_ADDRESS"))) {
			viewCache.tvAddress.setText(data.get(position).get("PARK_ADDRESS").toString());
		}

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, ParkDetailAct.class);
				intent.putExtra("code", data.get(position).get("CODE").toString() + ",");
				intent.putExtra("ownerName", data.get(position).get("PARK_NAME").toString());
				intent.putExtra("ownerPhone", data.get(position).get("PARK_PHONE").toString());
				context.startActivity(intent);

			}
		});
		return rowView;
	}

	private void getOwnerInfo(String releaseType, String userCode) {
		String url = PathConfig.ADDRESS + "/base/buser/queryReleaseInfo?releaseType=" + releaseType + "&userCode="
				+ userCode;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				Map<String, String> data = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				ownerPhone = data.get("PHONE");
				ownerName = data.get("NAME");

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private class ViewHolder {

		public TextView tvHourPrice, tvMonthPirce, tvAddress;

	}

	public void setData(List<HashMap<String, String>> resultList) {
		this.data = resultList;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}