package com.bepo.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.utils.MyTextUtils;
import com.dykj.contradiction.EventDetail;

@SuppressLint("UseSparseArrays")
@SuppressWarnings("rawtypes")
public class EventWorkAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<Map<String, Object>> data;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public EventWorkAdapter(ArrayList<Map<String, Object>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public EventWorkAdapter(ArrayList<Map<String, Object>> data, ListView listView, Context context) {
		super(data, context);
		this.listView = listView;
		this.context = context;
		this.data = (List<Map<String, Object>>) data;
		inflater = ((Activity) context).getLayoutInflater();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) context;

		View rowView = convertView;
		final ViewHolder viewCache;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.workflow_items1, null);
			viewCache = new ViewHolder();

			viewCache.tvCaller = (TextView) rowView.findViewById(R.id.tvCaller);
			viewCache.tvActionName = (TextView) rowView.findViewById(R.id.tvActionName);
			viewCache.tvOwnerName = (TextView) rowView.findViewById(R.id.tvOwnerName);
			viewCache.tvOpinion_Date = (TextView) rowView.findViewById(R.id.tvOpinion_Date);
			viewCache.tvOpinion = (TextView) rowView.findViewById(R.id.tvOpinion);
			viewCache.tvyizhuandao = (TextView) rowView.findViewById(R.id.tvyizhuandao);

			// viewCache.iv00 = (ImageView) rowView.findViewById(R.id.iv00);
			viewCache.iv01 = (ImageView) rowView.findViewById(R.id.iv01);
			viewCache.ivCircle = (ImageView) rowView.findViewById(R.id.ivCircle);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("CALLER_NAME"))) {
			viewCache.tvCaller.setText(data.get(position).get("CALLER_NAME").toString());
			viewCache.tvCaller.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		}

		if (!(null == data.get(position).get("ACTION_NAME"))) {
			viewCache.tvActionName.setText(data.get(position).get("ACTION_NAME").toString());
		}

		if (!(null == data.get(position).get("ACTION_NAME"))) {
			viewCache.tvOpinion_Date.setText(data.get(position).get("OPINION_DATE").toString());
		}

		if (!(null == data.get(position).get("OPINION"))) {
			viewCache.tvOpinion.setText(data.get(position).get("OPINION").toString());
		} else {
			// viewCache.tvOpinion.setVisibility(View.GONE);
		}

		if (!(null == data.get(position).get("OWNER_NAME"))) {
			viewCache.tvOwnerName.setText(data.get(position).get("OWNER_NAME").toString());
			viewCache.tvOwnerName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		} else {
			viewCache.tvOwnerName.setVisibility(View.GONE);
			viewCache.tvyizhuandao.setVisibility(View.GONE);

		}

		return rowView;
	}

	private class ViewHolder {

		public TextView tvCaller;
		public TextView tvActionName;
		public TextView tvOwnerName;
		public TextView tvOpinion_Date;
		public TextView tvOpinion;
		public TextView tvyizhuandao;
		public ImageView iv00;
		public ImageView iv01;
		public ImageView ivCircle;

	}

	public void setData(List<Map<String, Object>> resultList) {
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