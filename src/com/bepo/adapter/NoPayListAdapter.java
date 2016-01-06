package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.ui.CarPortOrderDetails;
import com.zxing.EPQRAct;

@SuppressWarnings("rawtypes")
public class NoPayListAdapter extends CustomAdapter {
	private ListView listView;
	private Context context;
	private List<HashMap<String, String>> data;
	private static HashMap<Integer, String> flag;
	private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public NoPayListAdapter(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
	}

	@SuppressWarnings("unchecked")
	public NoPayListAdapter(ArrayList<HashMap<String, String>> data, ListView listView, Context context) {
		super(data, context);
		this.listView = listView;
		this.context = context;
		this.data = (ArrayList<HashMap<String, String>>) data;
		flag = new HashMap<Integer, String>();
		initData();
		inflater = ((Activity) context).getLayoutInflater();
	}

	private void initData() {
		for (int i = 0; i < data.size(); i++) {
			getIsSelected().put(i, data.get(i).get("CODE_ORDER_STATUS_NAME"));
		}

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) context;

		View rowView = convertView;
		final ViewHolder viewCache;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.carowner_order_items, null);
			viewCache = new ViewHolder();
			viewCache.tvCreatTime = (TextView) rowView.findViewById(R.id.tvCreatTime);
			viewCache.tvOrderNumber = (TextView) rowView.findViewById(R.id.tvOrderNumber);

			viewCache.tvStartTime = (TextView) rowView.findViewById(R.id.tvStartTime);
			viewCache.tvEndTime = (TextView) rowView.findViewById(R.id.tvEndTime);
			viewCache.tvParkName = (TextView) rowView.findViewById(R.id.tvParkName);
			viewCache.tvParkNum = (TextView) rowView.findViewById(R.id.tvParkNum);

			viewCache.tvRentType = (TextView) rowView.findViewById(R.id.tvRentType);
			viewCache.tvOrderDetail = (TextView) rowView.findViewById(R.id.tvOrderDetail);
			viewCache.tvPriceCount = (TextView) rowView.findViewById(R.id.tvPriceCount);
			viewCache.tvStatus = (TextView) rowView.findViewById(R.id.tvStatus);
			viewCache.tvDanjia = (TextView) rowView.findViewById(R.id.tvDanjia);
			viewCache.tvDanwei = (TextView) rowView.findViewById(R.id.tvDanwei);
			viewCache.rlPay = (RelativeLayout) rowView.findViewById(R.id.rlPay);
			viewCache.rlzxing = (RelativeLayout) rowView.findViewById(R.id.rlzxing);

			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();
		}

		if (!(null == data.get(position).get("CREATED_DATE"))) {
			viewCache.tvCreatTime.setText(data.get(position).get("CREATED_DATE").toString());
		}

		if (!(null == data.get(position).get("ORDER_NUMBER"))) {
			viewCache.tvOrderNumber.setText(data.get(position).get("ORDER_NUMBER").toString());
		}

		if (!(null == data.get(position).get("BEGIN_TIME"))) {
			String beginTime = data.get(position).get("BEGIN_TIME").toString();
			viewCache.tvStartTime.setText(beginTime);
		}

		if (!(null == data.get(position).get("END_TIME"))) {
			String endTime = data.get(position).get("END_TIME").toString();
			viewCache.tvEndTime.setText(endTime);
		}

		if (!(null == data.get(position).get("PARK_ADDRESS"))) {
			String parkName = data.get(position).get("PARK_ADDRESS").toString();
			viewCache.tvParkName.setText(parkName);
		}

		if (MyTextUtils.isEmpty(data.get(position).get("PARK_NUMBER"))
				&& MyTextUtils.isEmpty(data.get(position).get("PLATE"))) {
			viewCache.tvParkNum.setText("进场自选");
			// viewCache.linSiren.setVisibility(View.GONE);
		} else {

			String parkNum = "";
			String ParkPlate = "";

			if (!(null == data.get(position).get("PARK_NUMBER"))) {
				parkNum = data.get(position).get("PARK_NUMBER");
			}

			if (!(null == data.get(position).get("PLATE"))) {
				ParkPlate = data.get(position).get("PLATE");
			}
			viewCache.tvParkNum.setText(parkNum + " " + ParkPlate);

		}

		if (!(null == data.get(position).get("CODE_RENT_TYPE_NAME"))) {
			viewCache.tvRentType.setText(data.get(position).get("CODE_RENT_TYPE_NAME").toString());
			if (data.get(position).get("CODE_RENT_TYPE_NAME").toString().equals("时租")) {
				viewCache.tvDanjia.setText(data.get(position).get("PRICE_HOUR"));
				viewCache.tvDanwei.setText("元/小时");
			} else {
				viewCache.tvDanjia.setText(data.get(position).get("PRICE_MONTH"));
				viewCache.tvDanwei.setText("元/月");
			}
		}
		if (!(null == data.get(position).get("RENT_NUMBER"))) {
			viewCache.tvOrderDetail.setText(data.get(position).get("RENT_NUMBER").toString());
		}
		if (!(null == data.get(position).get("PRICE_COUNT"))) {
			viewCache.tvPriceCount.setText(data.get(position).get("PRICE_COUNT").toString());
		}

		viewCache.rlPay.setVisibility(View.GONE);
		viewCache.rlzxing.setVisibility(View.GONE);
		if (getIsSelected().get(position).equals("待支付")) {
			viewCache.rlPay.setVisibility(View.VISIBLE);
		} else if (getIsSelected().get(position).equals("已支付")) {
			viewCache.rlzxing.setVisibility(View.VISIBLE);
		} else if (getIsSelected().get(position).equals("订单超时")) {

		}

		viewCache.tvStatus.setText(getIsSelected().get(position));

//		viewCache.rlJieSuan.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				jiesuan(data.get(position).get("CODE").toString());
//
//			}
//		});
		viewCache.rlPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, CarPortOrderDetails.class);
				intent.putExtra("code", data.get(position).get("CODE").toString());
				context.startActivity(intent);

			}
		});

		viewCache.rlzxing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(context, EPQRAct.class);
				intent.putExtra("code", data.get(position).get("ORDER_NUMBER").toString());
				context.startActivity(intent);

			}
		});

		return rowView;
	}

	public static HashMap<Integer, String> getIsSelected() {
		return flag;
	}

	private class ViewHolder {

		public TextView tvRentType, tvOrderNumber, tvOrderDetail, tvPriceCount, tvStatus, tvStartTime, tvEndTime,
				tvDanjia, tvCreatTime, tvDanwei, tvParkName, tvParkNum;
		public RelativeLayout rlPay, rlzxing, rlJieSuan, rlPingjia;
		public LinearLayout linBTN;

	}

	public void setData(List<HashMap<String, String>> resultList) {
		this.data = resultList;
	}

	private void jiesuan(String code) {
		String url = PathConfig.ADDRESS + "/trad/order/settlement?code=" + code;
		url = MyTextUtils.urlPlusAndFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				Map<String, String> map = new HashMap<String, String>();
				map = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (map.get("status").equals("true")) {
					ToastUtils.showSuperToastComment(context, "结算成功");
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
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