package com.yunt.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;

import com.bepo.R;
import com.bepo.utils.MyTextUtils;
import com.bepo.utils.TimeUtil;

public class HourFragment extends Fragment {

	NumberPicker npd, nph, npm;
	ArrayList<String> time;
	ArrayList<String> time2;
	RelativeLayout rlSubmit;
	String pickDate, pickTime, pickTime2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hour_fra, container, false);

		pickDate = TimeUtil.getToday();
		pickTime = "00点";
		pickTime2 = "00分";

		rlSubmit = (RelativeLayout) view.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (RentTime.isBusiness) {
					ParkDetailAct2b.setTimeZu(pickDate + " " + pickTime + ":" + pickTime2);
					ParkDetailAct2b.setCodeRentType("1848");
				} else {
					ParkDetailAct2.setTimeZu(pickDate + " " + pickTime + ":" + pickTime2);
					ParkDetailAct2.setCodeRentType("1848");
				}

				getActivity().finish();

			}
		});

		String[] city = { "今天", "明天" };
		npd = (NumberPicker) view.findViewById(R.id.numberPicker1);
		npd.setDisplayedValues(city);
		npd.setMinValue(0);
		npd.setMaxValue(city.length - 1);
		npd.setFocusable(false);
		npd.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npd.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
				switch (newVal) {
				case 0:
					pickDate = TimeUtil.getToday();
					break;
				case 1:
					pickDate = TimeUtil.getTomorrow();
					break;
				}

			}
		});

		// 时针数据 0点-23点
		time = new ArrayList<String>();
		for (int i = 0; i < 24; i++) {
			if (i < 10) {
				time.add("0" + i + "点");
			} else {
				time.add(i + "点");
			}

		}
		String[] array = new String[time.size()];
		time.toArray(array);

		nph = (NumberPicker) view.findViewById(R.id.numberPicker2);
		nph.setDisplayedValues(array);
		nph.setMinValue(0);
		nph.setMaxValue(array.length - 1);
		nph.setFocusable(false);
		nph.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		nph.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
				pickTime = time.get(newVal);
			}
		});

		// 分针数据 0分 - 55分
		time2 = new ArrayList<String>();
		for (int i = 0; i < 60; i = i + 5) {
			if (i < 10) {
				time2.add("0" + i + "分");
			} else {
				time2.add(i + "分");
			}

		}
		String[] array2 = new String[time2.size()];
		time2.toArray(array2);

		npm = (NumberPicker) view.findViewById(R.id.numberPicker3);
		npm.setDisplayedValues(array2);
		npm.setMinValue(0);
		npm.setMaxValue(array2.length - 1);
		npm.setFocusable(false);
		npm.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npm.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
				pickTime2 = time2.get(newVal);
			}
		});

		return view;
	}
}