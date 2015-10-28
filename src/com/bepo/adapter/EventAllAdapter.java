package com.bepo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.bepo.core.PathConfig;
import com.dykj.contradiction.EventDetail;

@SuppressWarnings("rawtypes")
public class EventAllAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<Map<String, Object>> data;
	private LayoutInflater inflater;
	private int status;
	private String flag = "";

	@SuppressWarnings("unchecked")
	public EventAllAdapter(ArrayList<Map<String, Object>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public EventAllAdapter(ArrayList<Map<String, Object>> data, ListView listView, Context context,
			int eventStatusFlag) {
		super(data, context);
		this.status = eventStatusFlag;
		this.listView = listView;
		this.context = context;
		this.data = (List<Map<String, Object>>) data;
		inflater = ((Activity) context).getLayoutInflater();
	}

	@SuppressWarnings("unchecked")
	public View getView(final int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) context;

		View rowView = convertView;
		final ViewHolder viewCache;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item, null);
			viewCache = new ViewHolder();
			viewCache.tvName = (TextView) rowView.findViewById(R.id.tvName);

			viewCache.tvAddress = (TextView) rowView.findViewById(R.id.tvAddress);
			viewCache.tvTime = (TextView) rowView.findViewById(R.id.tvTime);

			viewCache.tvContent = (TextView) rowView.findViewById(R.id.tvContent);

			viewCache.tvEventType = (TextView) rowView.findViewById(R.id.tvEventType);
			viewCache.tvEventStatus = (TextView) rowView.findViewById(R.id.tvEventStatus);
			viewCache.tvEventFrom = (TextView) rowView.findViewById(R.id.tvEventFrom);

			viewCache.tvSuqiuren = (TextView) rowView.findViewById(R.id.tvSuqiuren);
			viewCache.tvGridAddress = (TextView) rowView.findViewById(R.id.tvGridAddress);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		viewCache.tvName.setText(data.get(position).get("NAME_APPEAL").toString());
		viewCache.tvTime.setText(data.get(position).get("START_DATE").toString());

		viewCache.tvEventType.setText("类型：" + data.get(position).get("CODE_EVENT_TYPE_NAME"));

		if (data.get(position).get("STEP_NAME") == null) {

			viewCache.tvEventStatus.setVisibility(View.GONE);
		} else {
			viewCache.tvEventStatus.setText("状态：" + data.get(position).get("STEP_NAME"));
		}

		viewCache.tvEventFrom.setText("来自：" + data.get(position).get("CODE_EVENT_FROM_NAME"));

		if (data.get(position).get("APPEAL_NAME") != null) {

			viewCache.tvSuqiuren.setText(data.get(position).get("APPEAL_NAME").toString());
		}
		if (data.get(position).get("GRID_NAME") != null) {
			viewCache.tvGridAddress.setText(data.get(position).get("GRID_NAME").toString());
		}
		// viewCache.tvContent.setText(data.get(position).get("NAME_APPEAL"));

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (status == PathConfig.event_status_close) {
					flag = "event_close";
				} else if (status == PathConfig.event_status_todo) {
					flag = "event_todo";
				}
				Intent intent = new Intent(context, EventDetail.class);
				intent.putExtra("FLAG_WFID", data.get(position).get("FLAG_WFID").toString());
				intent.putExtra("STEP_ID", data.get(position).get("STEP_ID").toString());
				intent.putExtra("CODE", data.get(position).get("CODE").toString());
				intent.putExtra("flag", flag);
				context.startActivity(intent);

			}
		});

		return rowView;
	}

	private class ViewHolder {

		public TextView tvName;
		public TextView tvContent;
		public TextView tvTime;
		public TextView tvAddress;
		public TextView tvEventType;
		public TextView tvEventStatus;
		public TextView tvEventFrom;
		public TextView tvSuqiuren;
		public TextView tvGridAddress;

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