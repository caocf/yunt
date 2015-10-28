package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bepo.R;

@SuppressWarnings("rawtypes")
public class WalletAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public WalletAdapter(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public WalletAdapter(ArrayList<HashMap<String, String>> data, ListView listView, Context context) {
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
			rowView = inflater.inflate(R.layout.wallet_items, null);
			viewCache = new ViewHolder();

			viewCache.tvFuhao = (TextView) rowView.findViewById(R.id.tvFuhao);
			viewCache.tvDate = (TextView) rowView.findViewById(R.id.tvDate);
			viewCache.tvFrom = (TextView) rowView.findViewById(R.id.tvFrom);
			viewCache.tvMoney = (TextView) rowView.findViewById(R.id.tvMoney);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("CODE_BILL_FROM_NAME"))) {
			viewCache.tvFrom.setText(data.get(position).get("CODE_BILL_FROM_NAME"));
		}
		if (!(null == data.get(position).get("CREATED_DATE"))) {

			viewCache.tvDate.setText(data.get(position).get("CREATED_DATE"));
		}

		if (!data.get(position).get("CODE_BILL_TYPE_NAME").equals("Ö§³ö")) {
			viewCache.tvFuhao.setText("+");
			viewCache.tvFuhao.setTextColor(context.getResources().getColor(R.color.ticket_red));

			viewCache.tvMoney.setText(data.get(position).get("MONEY"));
			viewCache.tvMoney.setTextColor(context.getResources().getColor(R.color.ticket_red));
		} else {
			viewCache.tvFuhao.setText("-");
			viewCache.tvFuhao.setTextColor(context.getResources().getColor(R.color.tag_green));

			viewCache.tvMoney.setText(data.get(position).get("MONEY"));
			viewCache.tvMoney.setTextColor(context.getResources().getColor(R.color.tag_green));
		}

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		return rowView;
	}

	private class ViewHolder {
		public TextView tvDate, tvFrom, tvMoney, tvFuhao;
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