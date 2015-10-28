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
import com.yunt.ui.SubmitPark;

@SuppressWarnings("rawtypes")
public class MyCarportListAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public MyCarportListAdapter(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public MyCarportListAdapter(ArrayList<HashMap<String, String>> data, ListView listView, Context context) {
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
			rowView = inflater.inflate(R.layout.my_carport_items, null);
			viewCache = new ViewHolder();

			viewCache.tvHourPrice = (TextView) rowView.findViewById(R.id.tvHourPrice);
			viewCache.tvMonthPirce = (TextView) rowView.findViewById(R.id.tvMonthPirce);
			viewCache.tvAddress = (TextView) rowView.findViewById(R.id.tvAddress);
			viewCache.tvType = (TextView) rowView.findViewById(R.id.tvType);

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
		if (!(null == data.get(position).get("CODE_PARK_TYPE_NAME"))) {
			viewCache.tvType.setText(data.get(position).get("CODE_PARK_TYPE_NAME").toString());
		}

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, SubmitPark.class);
				intent.putExtra("code", data.get(position).get("CODE").toString());
				context.startActivity(intent);

			}
		});
		return rowView;
	}

	private class ViewHolder {

		public TextView tvHourPrice, tvMonthPirce, tvAddress, tvType;

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