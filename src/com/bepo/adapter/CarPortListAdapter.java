package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bepo.R;
import com.yunt.ui.HomeAct2;
import com.yunt.ui.ParkDetailAct2;
import com.yunt.ui.ParkDetailAct2b;

public class CarPortListAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;

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

		View rowView = convertView;
		final ViewHolder viewCache;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.park_siren_list, null);
			viewCache = new ViewHolder();

			viewCache.tvXiaoqu = (TextView) rowView.findViewById(R.id.tvXiaoqu);
			viewCache.tvMapAddress = (TextView) rowView.findViewById(R.id.tvMapAddress);
			viewCache.tvHourPrice = (TextView) rowView.findViewById(R.id.tvHourPrice);
			viewCache.tvMonthPirce = (TextView) rowView.findViewById(R.id.tvMonthPirce);

			viewCache.tvCheWei = (TextView) rowView.findViewById(R.id.tvCheWei);
			viewCache.tvXingqi = (TextView) rowView.findViewById(R.id.tvXingqi);
			viewCache.tvTime = (TextView) rowView.findViewById(R.id.tvTime);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("PARK_ADDRESS"))) {
			viewCache.tvXiaoqu.setText(data.get(position).get("PARK_ADDRESS").toString());
		}

		if (!(null == data.get(position).get("ADDRESS"))) {
			viewCache.tvMapAddress.setText(data.get(position).get("ADDRESS").toString());
		}

		if (!(null == data.get(position).get("PRICE_HOUR"))) {
			viewCache.tvHourPrice.setText(data.get(position).get("PRICE_HOUR").toString() + "元/时");
		}

		if (!(null == data.get(position).get("PRICE_MONTH"))) {
			viewCache.tvMonthPirce.setText(data.get(position).get("PRICE_MONTH").toString() + "元/月");
		}

		// 车位编号和车牌号
		if (!(null == data.get(position).get("PLATE"))) {
			viewCache.tvMonthPirce.setText(data.get(position).get("PRICE_MONTH").toString() + "元/月");

			if (data.get(position).get("PLATE").length() > 1) {
				viewCache.tvCheWei.setText(data.get(position).get("PARK_NUMBER") + " "
						+ data.get(position).get("PLATE"));
			} else {
				viewCache.tvCheWei.setText(data.get(position).get("PARK_NUMBER"));
			}
		} else {
			viewCache.tvCheWei.setText(data.get(position).get("NOW_PARK") + "/"
					+ data.get(position).get("PARKS_NUM"));
		}

		// 可租时段
		String week = data.get(position).get("WEEKNAME");
		viewCache.tvXingqi.setText(week);

		if (data.get(position).get("ALL_TIME").equals("0")) {
			viewCache.tvTime.setText(data.get(position).get("START_TIME") + "-"
					+ data.get(position).get("END_TIME"));
		} else {
			viewCache.tvTime.setText("全天可租");
		}

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (HomeAct2.nowIsSiren) {
					Intent intent = new Intent(context, ParkDetailAct2.class);
					intent.putExtra("code", data.get(position).get("CODE").toString());
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context, ParkDetailAct2b.class);
					intent.putExtra("code", data.get(position).get("CODE").toString());
					context.startActivity(intent);
				}

			}
		});
		return rowView;
	}

	private class ViewHolder {
		TextView tvXiaoqu;// 小区
		TextView tvMapAddress;// 描述地址
		TextView tvCheWei;// 车位
		TextView tvXingqi;// 星期
		TextView tvTime;// 时间
		TextView tvHourPrice, tvMonthPirce;
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