package com.bepo.view;

import java.util.ArrayList;
import java.util.List;

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
import com.bepo.adapter.EventFromAdapter;
import com.bepo.bean.EventParameter;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author simas
 * @describe �¼�״̬�ĵ���pop
 * @time 2015-5-11 11:12:06
 * 
 */

public class SelectEventStatusPop extends PopupWindow {

	Context context;
	private View View;
	private ListView lvFrom;

	private EventFromAdapter<String> mEventFromAdapter;
	public static ArrayList<String> firstData = new ArrayList<String>();// һ���˵�����
	List list = new ArrayList<String>();

	@SuppressWarnings("deprecation")
	public SelectEventStatusPop(Activity context, OnClickListener itemsOnClick) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View = inflater.inflate(R.layout.event_from_dialog, null);

		lvFrom = (ListView) View.findViewById(R.id.lvFrom);
		mEventFromAdapter = new EventFromAdapter<String>(firstData, context);
		lvFrom.setAdapter(mEventFromAdapter);
		lvFrom.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int position, long arg3) {

				EventParameter mEventParameter = new EventParameter();
				mEventParameter.setEventStatus(list.get(position).toString());
				EventBus.getDefault().post(mEventParameter);
				dismiss();

			}
		});
		initData();
		// ����SelectPicPopupWindow��View
		this.setContentView(View);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.FILL_PARENT);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		// ����SelectPicPopupWindow�������嶯��Ч��
		this.setAnimationStyle(R.style.pop_roate);
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// ����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		// mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
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

	private void initData() {

		if (PathConfig.statusJson.equals("")) {
			firstData.clear();
			String url = PathConfig.ADDRESS + "/gsm/event/eevent/queryAllSteps";
			StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
				@SuppressWarnings("unchecked")
				@Override
				public void onResponse(String response) {

					String jsondata = response.toString();
					Log.d("�¼�״̬", jsondata);
					PathConfig.statusJson = jsondata;

					list = JSON.parseArray(jsondata, String.class);

					for (int i = 0; i < list.size(); i++) {
						firstData.add(list.get(i).toString().split(":")[1]);
					}

					Log.d("�¼�״̬", firstData.toString());
					mEventFromAdapter.notifyDataSetChanged();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e("TAG", error.getMessage(), error);
				}
			});
			ApplicationController.getInstance().addToRequestQueue(stringRequest);

		} else {
			firstData.clear();
			list = JSON.parseArray(PathConfig.statusJson, String.class);
			for (int i = 0; i < list.size(); i++) {
				firstData.add(list.get(i).toString().split(":")[1]);
			}
			Log.d("�¼�״̬", firstData.toString());
			mEventFromAdapter.notifyDataSetChanged();

		}

	}
}
