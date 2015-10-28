package com.bepo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xclcharts.common.IFormatterDoubleCallBack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.core.PathConfig;
import com.dykj.contradiction.EventDetail;

@SuppressWarnings("rawtypes")
public class DiaryAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<Map<String, Object>> data;
	private LayoutInflater inflater;
	private int status;
	private String flag = "";

	@SuppressWarnings("unchecked")
	public DiaryAdapter(ArrayList<Map<String, Object>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public DiaryAdapter(ArrayList<Map<String, Object>> data, ListView listView, Context context, int eventStatusFlag) {
		super(data, context);
		this.status = eventStatusFlag;
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
			rowView = inflater.inflate(R.layout.diary_list_item, null);
			viewCache = new ViewHolder();

			viewCache.tvTimeStartYear = (TextView) rowView.findViewById(R.id.tvTimeStartYear);
			viewCache.tvTimeStartHour = (TextView) rowView.findViewById(R.id.tvTimeStartHour);
			viewCache.tvTimeEndYear = (TextView) rowView.findViewById(R.id.tvTimeEndYear);
			viewCache.tvTimeEndHour = (TextView) rowView.findViewById(R.id.tvTimeEndHour);

			viewCache.tvXuncha = (TextView) rowView.findViewById(R.id.tvXuncha);
			viewCache.tvZoufang = (TextView) rowView.findViewById(R.id.tvZoufang);
			viewCache.tvWenti = (TextView) rowView.findViewById(R.id.tvWenti);

			viewCache.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
			viewCache.tvGridAddress = (TextView) rowView.findViewById(R.id.tvGridAddress);
			viewCache.tvPeople = (TextView) rowView.findViewById(R.id.tvPeople);

			viewCache.linXuncha = (LinearLayout) rowView.findViewById(R.id.linXuncha);
			viewCache.linZoufang = (LinearLayout) rowView.findViewById(R.id.linZoufang);
			viewCache.linWenti = (LinearLayout) rowView.findViewById(R.id.linWenti);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		String[] startTime = data.get(position).get("VISITING_TIME").toString().split(" ");
		String[] endTime = data.get(position).get("VISITING_TIMEEND").toString().split(" ");

		viewCache.tvTimeStartYear.setText(startTime[0]);
		viewCache.tvTimeStartHour.setText(startTime[1]);
		viewCache.tvTimeEndYear.setText(endTime[0]);
		viewCache.tvTimeEndHour.setText(endTime[1]);

		// 网格巡查情况
		if (data.get(position).get("INSPECTIONS_SITUATION") == null) {
			viewCache.linXuncha.setVisibility(View.GONE);
		} else {
			viewCache.tvXuncha.setText(data.get(position).get("INSPECTIONS_SITUATION").toString());
		}

		// 入户走访情况
		if (data.get(position).get("VISITED_CIRCUMSTANCES") == null) {
			viewCache.linZoufang.setVisibility(View.GONE);
		} else {
			viewCache.tvZoufang.setText(data.get(position).get("VISITED_CIRCUMSTANCES").toString());
		}

		// 问题处理情况
		if (data.get(position).get("CONTENTS") == null) {
			viewCache.linWenti.setVisibility(View.GONE);
		} else {
			viewCache.tvWenti.setText(data.get(position).get("CONTENTS").toString());
		}
		if (data.get(position).get("CREATED_DATE") != null) {
			viewCache.tvTime.setText(data.get(position).get("CREATED_DATE").toString());
		}

		viewCache.tvGridAddress.setText(data.get(position).get("GRID_NAME").toString());
		viewCache.tvPeople.setText(data.get(position).get("VISITS_DUTY").toString() + "  "
				+ data.get(position).get("INSPECTION_VISITS").toString());

		// rowView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// if (status == PathConfig.event_status_close) {
		// flag = "event_close";
		// } else if (status == PathConfig.event_status_todo) {
		// flag = "event_todo";
		// }
		// Intent intent = new Intent(context, EventDetail.class);
		// intent.putExtra("FLAG_WFID",
		// data.get(position).get("FLAG_WFID").toString());
		// intent.putExtra("STEP_ID",
		// data.get(position).get("STEP_ID").toString());
		// intent.putExtra("CODE", data.get(position).get("CODE").toString());
		// intent.putExtra("flag", flag);
		// context.startActivity(intent);
		//
		// }
		// });

		return rowView;
	}

	private class ViewHolder {

		public TextView tvXuncha;
		public TextView tvZoufang;
		public TextView tvWenti, tvTime, tvGridAddress, tvPeople;
		public TextView tvTimeStartYear, tvTimeStartHour, tvTimeEndYear, tvTimeEndHour;

		public LinearLayout linXuncha;
		public LinearLayout linZoufang;
		public LinearLayout linWenti;

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