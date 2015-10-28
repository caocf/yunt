package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.yunt.ui.ParkDetailAct;
import com.yunt.ui.MessageInfo;

@SuppressWarnings("rawtypes")
public class MessageAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public MessageAdapter(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public MessageAdapter(ArrayList<HashMap<String, String>> data, ListView listView, Context context) {
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
			rowView = inflater.inflate(R.layout.message_items, null);
			viewCache = new ViewHolder();

			viewCache.tvReadState = (TextView) rowView.findViewById(R.id.tvReadState);
			viewCache.tvMessage = (TextView) rowView.findViewById(R.id.tvMessage);
			viewCache.tvTime = (TextView) rowView.findViewById(R.id.tvTime);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("CODE_READED_NAME"))) {
			viewCache.tvReadState.setText(data.get(position).get("CODE_READED_NAME").toString());
		}

		if (!(null == data.get(position).get("MESSAGE"))) {
			viewCache.tvMessage.setText(data.get(position).get("MESSAGE").toString() + ":");
		}

		if (!(null == data.get(position).get("CREATED_DATE"))) {
			viewCache.tvTime.setText(data.get(position).get("CREATED_DATE").toString());
		}
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, MessageInfo.class);
				intent.putExtra("code", data.get(position).get("CODE").toString());
				context.startActivity(intent);

			}
		});
		return rowView;
	}

	private class ViewHolder {
		public TextView tvReadState, tvMessage, tvTime;
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