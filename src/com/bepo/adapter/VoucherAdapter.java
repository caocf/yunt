package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bepo.R;
import com.github.johnpersano.supertoasts.R.string;
import com.yunt.ui.ParkDetailAct;
import com.yunt.ui.MessageInfo;

@SuppressWarnings("rawtypes")
public class VoucherAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public VoucherAdapter(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public VoucherAdapter(ArrayList<HashMap<String, String>> data, ListView listView, Context context) {
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
			rowView = inflater.inflate(R.layout.voucher_items, null);
			viewCache = new ViewHolder();

			viewCache.tvExprireTime = (TextView) rowView.findViewById(R.id.tvExprireTime);
			viewCache.tvMoney = (TextView) rowView.findViewById(R.id.tvMoney);
			viewCache.tvYiguoqi = (TextView) rowView.findViewById(R.id.tvYiguoqi);
			viewCache.tvYuan = (TextView) rowView.findViewById(R.id.tvYuan);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("MONEY"))) {
			if (data.get(position).get("CODE_CHIT_STATUS_NAME").equals("Œ¥ π”√")) {
				viewCache.tvMoney.setTextColor(context.getResources().getColor(R.color.ticket_red));

				viewCache.tvYiguoqi.setVisibility(View.GONE);
			} else {
				viewCache.tvMoney.setTextColor(context.getResources().getColor(R.color.ticket_gray));
				viewCache.tvYuan.setTextColor(context.getResources().getColor(R.color.ticket_gray));
				viewCache.tvYiguoqi.setVisibility(View.VISIBLE);
				viewCache.tvYiguoqi.setText("(" + data.get(position).get("CODE_CHIT_STATUS_NAME") + ")");
			}

			double temp = Double.parseDouble(data.get(position).get("MONEY").toString().trim());
			viewCache.tvMoney.setText((int) temp + "");
		}

		if (!(null == data.get(position).get("EXPIRE_TIME"))) {
			viewCache.tvExprireTime.setText(data.get(position).get("EXPIRE_TIME").toString().split(" ")[0]);
		}

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		return rowView;
	}

	private class ViewHolder {
		public TextView tvExprireTime, tvMoney, tvYiguoqi, tvYuan;
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