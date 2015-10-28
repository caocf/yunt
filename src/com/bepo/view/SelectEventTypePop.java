package com.bepo.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.FirstEventTypeAdapter;
import com.bepo.adapter.SecondEventTypeAdapter;
import com.bepo.bean.EventParameter;
import com.bepo.bean.EventTypeBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;

import de.greenrobot.event.EventBus;

public class SelectEventTypePop extends PopupWindow {

	Context context;
	private View View;
	private ListView lvFirstType, lvSecondType;

	private FirstEventTypeAdapter<String> firstEventTypeAdapter;
	private SecondEventTypeAdapter<String> secondEventTypeAdapter;

	public static ArrayList<EventTypeBean> metaData = new ArrayList<EventTypeBean>();// 元数据
	public static ArrayList<EventTypeBean> firstDataList = new ArrayList<EventTypeBean>();// 一级菜单数据
	public static ArrayList<EventTypeBean> secondDataList = new ArrayList<EventTypeBean>();// 二级菜单数据

	public static ArrayList<String> firstData = new ArrayList<String>();// 一级菜单数据
	public static ArrayList<String> secondData = new ArrayList<String>();// 二级菜单数据

	@SuppressWarnings("deprecation")
	public SelectEventTypePop(Activity context, OnClickListener itemsOnClick) {
		super(context);
		initData();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View = inflater.inflate(R.layout.event_type_dialog, null);

		// 一级菜单
		lvFirstType = (ListView) View.findViewById(R.id.lvFirstType);
		firstEventTypeAdapter = new FirstEventTypeAdapter<String>(firstData, context);
		lvFirstType.setAdapter(firstEventTypeAdapter);
		lvFirstType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int position, long arg3) {
				FirstEventTypeAdapter.setSelectedPosition(position);
				firstEventTypeAdapter.notifyDataSetChanged();
				secondDataMapper(firstDataList.get(position).getCODE());
				secondEventTypeAdapter.notifyDataSetChanged();
			}
		});

		// 二级菜单
		lvSecondType = (ListView) View.findViewById(R.id.lvSecondType);
		secondEventTypeAdapter = new SecondEventTypeAdapter<String>(secondData, context);
		lvSecondType.setAdapter(secondEventTypeAdapter);
		lvSecondType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int position, long arg3) {
				EventParameter mEventParameter = new EventParameter();
				mEventParameter.setEventType(secondDataList.get(position));
				EventBus.getDefault().post(mEventParameter);
				dismiss();
			}
		});

		// 设置SelectPicPopupWindow的View
		this.setContentView(View);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.SelectEventTypeStyle);
		// 实例化一个ColorDrawable颜色为半透明
//		 ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		View.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = View.findViewById(R.id.event_type_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height || y > height) {
						dismiss();
					}
				}
				int width = View.findViewById(R.id.event_type_layout).getLeft();
				int x = (int) event.getX();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (x < width || x > width) {
						dismiss();
					}
				}

				return true;
			}
		});

	}

	// 一级菜单数据处理
	private void firstDataMapper() {
		firstData.clear();
		firstDataList.clear();
		for (EventTypeBean temp : metaData) {
			if (temp.getPARENT_CODE().equals("0000")) {
				firstData.add(temp.getNAME_C());
				firstDataList.add(temp);

			}
		}

		if (secondData.isEmpty()) {
			for (EventTypeBean temp : metaData) {
				if (temp.getPARENT_CODE().equals(firstDataList.get(0).getCODE())) {
					secondData.add(temp.getNAME_C());
				}

			}

		}
		firstEventTypeAdapter.notifyDataSetChanged();
		secondEventTypeAdapter.notifyDataSetChanged();
	};

	// 二级菜单数据处理
	public static void secondDataMapper(String firstStr) {
		secondData.clear();
		secondDataList.clear();
		for (EventTypeBean temp : metaData) {
			if (temp.getPARENT_CODE().equals(firstStr)) {
				secondData.add(temp.getNAME_C());
				secondDataList.add(temp);
			}

		}

	}

	private void initData() {
		metaData.clear();
		String url = PathConfig.ADDRESS + "gsm/sys/sysdic/queryManageTree?name_dic=CODE_EVENT_TYPE";
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				String jsondata = response.toString();
				metaData = (ArrayList<EventTypeBean>) JSON.parseArray(jsondata, EventTypeBean.class);
				firstDataMapper();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("TAG", error.getMessage(), error);
			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}
}
