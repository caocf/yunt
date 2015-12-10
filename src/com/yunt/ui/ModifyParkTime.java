package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.WeekAdapter;
import com.bepo.bean.WeekBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.kyleduo.switchbutton.SwitchButton;

public class ModifyParkTime extends BaseAct {

	TextView tvCancle, tvCiri;
	GridView weekgridview;
	WeekAdapter weekAdapter;
	SwitchButton mSwitchButton;
	LinearLayout linMain, linSettingTime;
	NumberPicker nps, npe;
	RelativeLayout rlSubmit;

	String code;
	public HashMap<String, String> detailMap;
	ArrayList<HashMap<String, String>> weekData = new ArrayList<HashMap<String, String>>();

	// 时间
	public static String allTime;
	public static String startTime = "";
	public static String endTime = "";
	// 可租日期
	public static String week = "";
	ArrayList<String> time;
	TextView tvDay, tvNight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
		setContentView(R.layout.modify_park_time);
		code = getIntent().getExtras().getString("code");
		initView();
		getParkInfo();

	}

	private void initView() {
		tvCiri = (TextView) this.findViewById(R.id.tvCiri);
		tvDay = (TextView) this.findViewById(R.id.tvDay);
		tvDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nps.setValue(36);
				npe.setValue(68);
				startTime = "09:00";
				endTime = "17:00";
				isCiri();
			}
		});

		tvNight = (TextView) this.findViewById(R.id.tvNight);
		tvNight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nps.setValue(72);
				npe.setValue(28);
				startTime = "18:00";
				endTime = "07:00";
				isCiri();

			}
		});
		weekgridview = (GridView) this.findViewById(R.id.weekgridview);
		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (allTime.equals("0")) {

					// 判断时间间隔
					String ss = MyTextUtils.noSpace(startTime);
					String ee = MyTextUtils.noSpace(endTime);

					int s = Integer.parseInt(ss.replace(":", ""));
					int e = Integer.parseInt(ee.replace(":", ""));
					if (s - e == 0) {
						ToastUtils.showSuperToastAlert(getApplicationContext(), "开始时间不能等于结束时间");
						return;
					}
				}

				// 判断可租日期
				if (WeekBean.week.size() > 0) {
					week = "";
					for (String temp : WeekBean.week) {
						week = week + "," + temp;
					}
					if (week.split(",")[0].equals("")) {
						week = week.substring(1);
					}
					// ToastUtils.showSuperToastAlert(getApplicationContext(),
					// week);
				} else {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "可租日期不能为空");
					return;
				}

				submitTime();

			}

		});

		linSettingTime = (LinearLayout) this.findViewById(R.id.linSettingTime);
		tvCancle = (TextView) this.findViewById(R.id.tvCancle);
		tvCancle.setText("车位详情");
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		time = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			String s = i + " ";
			if (i < 10) {
				s = "0" + s;
			}
			time.add(s + ": " + "00");
			time.add(s + ": " + "15");
			time.add(s + ": " + "30");
			time.add(s + ": " + "45");
		}

		String[] array = new String[time.size()];
		time.toArray(array);

		nps = (NumberPicker) findViewById(R.id.numberPicker1);
		nps.setDisplayedValues(array);
		nps.setMinValue(0);
		nps.setMaxValue(array.length - 1);
		nps.setFocusable(false);
		nps.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		nps.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
				startTime = time.get(newVal);
				isCiri();
			}
		});

		npe = (NumberPicker) findViewById(R.id.numberPicker2);
		npe.setDisplayedValues(array);
		npe.setMinValue(0);
		npe.setMaxValue(array.length - 1);
		npe.setFocusable(false);
		npe.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npe.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
				endTime = time.get(newVal);
				isCiri();
			}
		});

		mSwitchButton = (SwitchButton) this.findViewById(R.id.sb_isAllDay);
		mSwitchButton.setChecked(false);
		mSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				linSettingTime.setVisibility(isChecked ? View.GONE : View.VISIBLE);
				allTime = isChecked ? "1" : "0";
			}
		});

	}

	protected void isCiri() {
		if (allTime.equals("0")) {

			// 判断时间间隔
			String ss = MyTextUtils.noSpace(startTime);
			String ee = MyTextUtils.noSpace(endTime);

			int s = Integer.parseInt(ss.replace(":", ""));
			int e = Integer.parseInt(ee.replace(":", ""));
			if (s - e < 0) {
				tvCiri.setVisibility(View.GONE);
			} else {
				tvCiri.setVisibility(View.VISIBLE);
			}
		}

	}

	private void getParkInfo() {

		String url = PathConfig.ADDRESS + "/base/breleasepark/info?code=" + code;
		url = MyTextUtils.urlPlusAndFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<HashMap<String, String>>() {
				});

				startTime = detailMap.get("START_TIME");
				endTime = detailMap.get("END_TIME");
				allTime = detailMap.get("ALL_TIME");

				// 判断时间间隔
				String ss = MyTextUtils.noSpace(startTime);
				String ee = MyTextUtils.noSpace(endTime);

				int s = Integer.parseInt(ss.replace(":", ""));
				int e = Integer.parseInt(ee.replace(":", ""));
				if (s - e < 0) {
					tvCiri.setVisibility(View.GONE);
				} else {
					tvCiri.setVisibility(View.VISIBLE);
				}

				for (int i = 0; i < time.size(); i++) {
					// Log.e("time", time.get(i).toString() + "=========" +
					// detailMap.get("START_TIME"));

					if (MyTextUtils.noSpace(time.get(i)).equals(ss)) {
						nps.setValue(i);
					}
					if (MyTextUtils.noSpace(time.get(i)).equals(ee)) {
						npe.setValue(i);
					}
				}

				if (detailMap.get("ALL_TIME").equals("0")) {
					mSwitchButton.setChecked(false);
				} else {
					mSwitchButton.setChecked(true);
				}

				initWeek();

			}

			private void initWeek() {
				ArrayList<HashMap<String, String>> weekData = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("NAME_C", "星期一");
				temp.put("weekcode", "1");
				weekData.add(temp);

				temp = new HashMap<String, String>();
				temp.put("NAME_C", "星期二");
				temp.put("weekcode", "2");
				weekData.add(temp);

				temp = new HashMap<String, String>();
				temp.put("NAME_C", "星期三");
				temp.put("weekcode", "3");
				weekData.add(temp);

				temp = new HashMap<String, String>();
				temp.put("NAME_C", "星期四");
				temp.put("weekcode", "4");
				weekData.add(temp);

				temp = new HashMap<String, String>();
				temp.put("NAME_C", "星期五");
				temp.put("weekcode", "5");
				weekData.add(temp);

				temp = new HashMap<String, String>();
				temp.put("NAME_C", "星期六");
				temp.put("weekcode", "6");
				weekData.add(temp);

				temp = new HashMap<String, String>();
				temp.put("NAME_C", "星期日");
				temp.put("weekcode", "0");
				weekData.add(temp);

				// 可租日期
				String week00 = detailMap.get("WEEKNAME");
				String[] temp02 = week00.split("、");

				// 处理星期控件点亮状态
				List<Boolean> isClicked = new ArrayList<Boolean>();
				isClicked.add(0, false);
				isClicked.add(1, false);
				isClicked.add(2, false);
				isClicked.add(3, false);
				isClicked.add(4, false);
				isClicked.add(5, false);
				isClicked.add(6, false);

				WeekBean.week.clear();
				// 接收后台数据
				for (int i = 0; i < temp02.length; i++) {

					if (temp02[i].equals("星期日")) {
						isClicked.set(6, true);
						WeekBean.week.add("0");
					}

					if (temp02[i].equals("星期一")) {
						isClicked.set(0, true);
						WeekBean.week.add("1");
					}

					if (temp02[i].equals("星期二")) {
						isClicked.set(1, true);
						WeekBean.week.add("2");
					}

					if (temp02[i].equals("星期三")) {
						isClicked.set(2, true);
						WeekBean.week.add("3");
					}

					if (temp02[i].equals("星期四")) {
						isClicked.set(3, true);
						WeekBean.week.add("4");
					}

					if (temp02[i].equals("星期五")) {
						isClicked.set(4, true);
						WeekBean.week.add("5");
					}

					if (temp02[i].equals("星期六")) {
						isClicked.set(5, true);
						WeekBean.week.add("6");
					}

				}

				weekAdapter = new WeekAdapter(weekData, isClicked, ModifyParkTime.this);
				weekgridview.setAdapter(weekAdapter);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	protected void submitTime() {

		String url;

		url = PathConfig.ADDRESS + "/base/bwake/add";
		url = MyTextUtils.urlPlusFoot(url);

		Map<String, String> params = new HashMap<String, String>();
		params.put("parkCode", detailMap.get("CODE"));
		params.put("allTime", allTime);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("week", week);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					dismissDialog();
					finish();
					ToastUtils.showSuperToastAlertGreen(ModifyParkTime.this, message.get("info"));
				} else {
					ToastUtils.showSuperToastAlert(ModifyParkTime.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(ModifyParkTime.this, "连接服务器失败,请稍后重试!");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(200 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

}
