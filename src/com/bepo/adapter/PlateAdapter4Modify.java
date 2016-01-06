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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bepo.R;
import com.yunt.ui.ChoosePlate;
import com.yunt.ui.DialogAct1;
import com.yunt.ui.ParkDetailAct2;
import com.yunt.ui.ParkDetailAct2b;

@SuppressWarnings("rawtypes")
public class PlateAdapter4Modify extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public PlateAdapter4Modify(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public PlateAdapter4Modify(ArrayList<HashMap<String, String>> data, ListView listView, Context context) {
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
			viewCache = new ViewHolder();
			rowView = inflater.inflate(R.layout.plate_items, null);
			viewCache.tvPlate = (TextView) rowView.findViewById(R.id.tvPlate);
			viewCache.ivDel = (ImageView) rowView.findViewById(R.id.ivDel);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		viewCache.tvPlate.setText(data.get(position).get("PLATE1") + data.get(position).get("PLATE2")
				+ data.get(position).get("PLATE3"));

		viewCache.ivDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent2 = new Intent(context, DialogAct1.class);
				intent2.putExtra("code", data.get(position).get("CODE").toString());
				context.startActivity(intent2);
			}
		});

		

		return rowView;
	}

	private class ViewHolder {
		public TextView tvPlate;
		public ImageView ivDel;
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