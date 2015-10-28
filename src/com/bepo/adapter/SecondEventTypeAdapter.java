package com.bepo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.bean.EventTypeBean;

public class SecondEventTypeAdapter<T> extends BaseAdapter {
	public ArrayList<String> data;
	public Context context;
	public LayoutInflater inflater;
	private static int selectedPosition = 0;

	public SecondEventTypeAdapter(ArrayList<String> data, Context context) {
		this.data = data;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		ViewHolder viewCache;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.event_type_second_item, null);
			viewCache = new ViewHolder();
			viewCache.tvSecond = (TextView) rowView.findViewById(R.id.tvSecond);
			viewCache.linSecondItem = (LinearLayout) rowView.findViewById(R.id.linSecondItem);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();

		}

		viewCache.tvSecond.setText(data.get(position));
		return rowView;
	}

	private class ViewHolder {

		public TextView tvSecond;

		public LinearLayout linSecondItem;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

}
