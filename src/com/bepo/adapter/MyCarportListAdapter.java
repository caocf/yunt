package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bepo.R;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.ui.DialogAct;
import com.yunt.ui.ModifyPark;

@SuppressWarnings("rawtypes")
public class MyCarportListAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, Object>> data;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public MyCarportListAdapter(ArrayList<HashMap<String, Object>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public MyCarportListAdapter(ArrayList<HashMap<String, Object>> data, ListView listView, Context context) {
		super(data, context);
		this.listView = listView;
		this.context = context;
		this.data = (ArrayList<HashMap<String, Object>>) data;
		inflater = ((Activity) context).getLayoutInflater();
	}

	@SuppressWarnings("unchecked")
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
			viewCache.tvWeek = (TextView) rowView.findViewById(R.id.tvWeek);
			viewCache.tvTime = (TextView) rowView.findViewById(R.id.tvTime);
			viewCache.tvBianhao = (TextView) rowView.findViewById(R.id.tvBianhao);
			viewCache.tvChePai = (TextView) rowView.findViewById(R.id.tvChePai);
			viewCache.rlBianji = (RelativeLayout) rowView.findViewById(R.id.rlBianji);
			viewCache.rlDel = (RelativeLayout) rowView.findViewById(R.id.rlDel);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("PARK_NUMBER"))) {

			if (!MyTextUtils.isEmpty(data.get(position).get("PARK_NUMBER").toString())) {

				if (!(null == data.get(position).get("GARAGE"))) {
					if (!data.get(position).get("GARAGE").equals("")) {
						viewCache.tvBianhao.setText(data.get(position).get("GARAGE").toString()
								+ data.get(position).get("PARK_NUMBER").toString());
					} else {
						viewCache.tvBianhao.setText(data.get(position).get("PARK_NUMBER").toString());
					}

				}
				if (!(null == data.get(position).get("PLATE"))) {
					if (data.get(position).get("PLATE").toString().length() == 1) {
						viewCache.tvChePai.setVisibility(View.GONE);
					} else {
						viewCache.tvChePai.setText(data.get(position).get("PLATE").toString());
					}

				}
			} else {
				viewCache.tvChePai.setVisibility(View.GONE);
				if (!data.get(position).get("GARAGE").equals("")) {
					viewCache.tvBianhao.setText(data.get(position).get("GARAGE").toString()
							+ data.get(position).get("PLATE").toString());
				} else {
					viewCache.tvBianhao.setText(data.get(position).get("PLATE").toString());
				}
			}

		}

		if (!(null == data.get(position).get("PRICE_HOUR"))) {
			viewCache.tvHourPrice.setText(data.get(position).get("PRICE_HOUR").toString() + "元/时");
		}
		if (!(null == data.get(position).get("PRICE_HOUR"))) {
			viewCache.tvHourPrice.setText(data.get(position).get("PRICE_HOUR").toString() + "元/时");
		}

		if (!data.get(position).get("wakeList").toString().equals("[]")) {
			String s = data.get(position).get("wakeList").toString();

			ArrayList<HashMap<String, String>> temp = (ArrayList<HashMap<String, String>>) JSON.parseObject(s,
					new TypeReference<ArrayList<HashMap<String, String>>>() {
					});

			if (temp.get(0).get("ALL_TIME").equals("1")) {
				viewCache.tvTime.setText("24小时可租");
			} else {

				// 判断时间间隔
				String ss = MyTextUtils.noSpace(temp.get(0).get("START_TIME"));
				String ee = MyTextUtils.noSpace(temp.get(0).get("END_TIME"));

				int sss = Integer.parseInt(ss.replace(":", ""));
				int eee = Integer.parseInt(ee.replace(":", ""));
				if (sss - eee >= 0) {
					viewCache.tvTime.setText(ss + " — " + ee + "(次日)");
				} else {
					viewCache.tvTime.setText(ss + " — " + ee);
				}

			}

			// 星期
			String sWeek = "";
			for (Map<String, String> map : temp) {
				sWeek = sWeek + "、" + map.get("WEEKNAME");
			}

			sWeek = sWeek.substring(1);
			sWeek = sWeek.replace("星期", "周");
			viewCache.tvWeek.setText(sWeek);
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
		if (!(null == data.get(position).get("CODE_PARK_TYPE"))) {

			String s = data.get(position).get("CODE_PARK_TYPE").toString();

			switch (Integer.parseInt(s.trim())) {
			case 1828:
				viewCache.tvType.setBackground(context.getResources().getDrawable(R.drawable.chewei_textbg_green));
				break;
			case 1829:
				viewCache.tvType.setBackground(context.getResources().getDrawable(R.drawable.chewei_textbg_red));
				break;
			case 1830:
				viewCache.tvType.setBackground(context.getResources().getDrawable(R.drawable.chewei_textbg_grey));
				viewCache.tvType.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
				viewCache.tvType.getPaint().setAntiAlias(true);// 抗锯齿
				break;
			case 1831:
				viewCache.tvType.setBackground(context.getResources().getDrawable(R.drawable.chewei_textbg_grey));
				break;
			case 1834:
				viewCache.tvType.setBackground(context.getResources().getDrawable(R.drawable.chewei_textbg_red));
				break;

			default:
				break;
			}

		}

		viewCache.rlBianji.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, ModifyPark.class);
				intent.putExtra("code", data.get(position).get("CODE").toString());
				context.startActivity(intent);

			}
		});
		viewCache.rlDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent2 = new Intent(context, DialogAct.class);
				intent2.putExtra("code", data.get(position).get("CODE").toString());
				context.startActivity(intent2);

			}
		});

		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, ModifyPark.class);
				intent.putExtra("code", data.get(position).get("CODE").toString());
				context.startActivity(intent);
			}
		});
		return rowView;
	}

	private class ViewHolder {

		public TextView tvHourPrice, tvMonthPirce, tvAddress, tvType, tvWeek, tvTime, tvBianhao, tvChePai;
		public RelativeLayout rlBianji, rlDel;

	}

	public void setData(List<HashMap<String, Object>> resultList) {
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