package com.bepo.view;

import java.util.ArrayList;
import java.util.Map;

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
import com.alibaba.fastjson.TypeReference;
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
 * @describe �¼������pop
 * @time 2015-5-11 15:17:19
 * 
 */

public class SelectEventAppraisePop extends PopupWindow {

	Context context;
	private View View;
	private ListView lvFrom;

	private EventFromAdapter<String> mEventFromAdapter;
	public static ArrayList<Map<String, String>> metaData = new ArrayList<Map<String, String>>();// Ԫ����
	public static ArrayList<String> firstData = new ArrayList<String>();// һ���˵�����

	@SuppressWarnings("deprecation")
	public SelectEventAppraisePop(Activity context, OnClickListener itemsOnClick) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View = inflater.inflate(R.layout.event_from_dialog, null);

		lvFrom = (ListView) View.findViewById(R.id.lvFrom);
		mEventFromAdapter = new EventFromAdapter<String>(firstData, context);
		lvFrom.setAdapter(mEventFromAdapter);
		lvFrom.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int position, long arg3) {
				metaData.get(position);
				EventParameter mEventParameter = new EventParameter();
				mEventParameter.setEventYesMap(metaData.get(position));
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
//		this.setAnimationStyle(R.style.SelectEventFromStyle);
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

		if (PathConfig.appraiseJson.equals("")) {
			metaData.clear();
			firstData.clear();
			String url = PathConfig.ADDRESS + "/gsm/sys/sysdic/queryManageTree?name_dic=CODE_APPRAISE";
			StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
				@SuppressWarnings("unchecked")
				@Override
				public void onResponse(String response) {

					String jsondata = response.toString();
					PathConfig.appraiseJson = jsondata;
					Log.d("�¼������", jsondata);
					metaData = JSON.parseObject(jsondata, new TypeReference<ArrayList<Map<String, String>>>() {
					});

					for (int i = 0; i < metaData.size(); i++) {
						firstData.add(metaData.get(i).get("NAME_C"));
					}
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

			metaData.clear();
			firstData.clear();
			metaData = JSON.parseObject(PathConfig.appraiseJson, new TypeReference<ArrayList<Map<String, String>>>() {
			});

			for (int i = 0; i < metaData.size(); i++) {
				firstData.add(metaData.get(i).get("NAME_C"));
			}
			mEventFromAdapter.notifyDataSetChanged();

		}

	}
}
