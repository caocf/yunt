package com.yunt.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.adapter.WeekAdapter;
import com.bepo.utils.MyTextUtils;
import com.kyleduo.switchbutton.SwitchButton;

public class RentalTime4SubmitFragment extends Fragment implements OnClickListener {

	public View view;
	GridView weekgridview;
	WeekAdapter weekAdapter;

	RelativeLayout rlStarTime, rlEndTime;
	LinearLayout linMain, linSettingTime;
	TimePop timePop;
	TextView tvStartTime, tvEndTime, tvCiri;
	private SwitchButton mSwitchButton;
	NumberPicker nps, npe;
	TextView tvDay, tvNight;

	public static String allTime;
	public static ArrayList<String> week = new ArrayList<String>();
	ArrayList<String> time;
	public static String startTime = "00:00";
	public static String endTime = "00:00";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.submit_rentaltime, container, false);
		weekgridview = (GridView) view.findViewById(R.id.weekgridview);
		tvCiri = (TextView) view.findViewById(R.id.tvCiri);
		linMain = (LinearLayout) view.findViewById(R.id.linMain);
		linSettingTime = (LinearLayout) view.findViewById(R.id.linSettingTime);

		tvDay = (TextView) view.findViewById(R.id.tvDay);
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

		tvNight = (TextView) view.findViewById(R.id.tvNight);
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

		nps = (NumberPicker) view.findViewById(R.id.numberPicker1);
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

		npe = (NumberPicker) view.findViewById(R.id.numberPicker2);
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

		mSwitchButton = (SwitchButton) view.findViewById(R.id.sb_isAllDay);
		mSwitchButton.setChecked(false);
		mSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				linSettingTime.setVisibility(isChecked ? View.GONE : View.VISIBLE);
				allTime = isChecked ? "1" : "0";
			}
		});

		initData();
		return view;
	}

	// public void onEventMainThread(Time4PopBean timeBean) {
	// if (timeBean.getFlag().equals("0")) {
	// // tvStartTime.setText(timeBean.getTime());
	// } else {
	// // tvEndTime.setText(timeBean.getTime());
	// }
	// }

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

	private void initData() {
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
		temp.put("NAME_C", "星期天");
		temp.put("weekcode", "0");
		weekData.add(temp);

		weekAdapter = new WeekAdapter(weekData, getActivity());
		weekgridview.setAdapter(weekAdapter);

		allTime = "0";

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}

	}
}
