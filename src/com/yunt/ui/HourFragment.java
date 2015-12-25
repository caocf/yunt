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

	NumberPicker nps, npe;
	ArrayList<String> time;
	RelativeLayout rlSubmit;
	String pickDate, pickTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hour_fra, container, false);

		pickDate = TimeUtil.getToday();
		pickTime = "00:00";

		rlSubmit = (RelativeLayout) view.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (RentTime.isBusiness) {
					ParkDetailAct2b.setTimeZu(pickDate + " " + MyTextUtils.noSpace(pickTime));
					ParkDetailAct2b.setCodeRentType("1848");
				} else {
					ParkDetailAct2.setTimeZu(pickDate + " " + MyTextUtils.noSpace(pickTime));
					ParkDetailAct2.setCodeRentType("1848");
				}

				getActivity().finish();

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

		String[] city = { "今天", "明天" };
		nps = (NumberPicker) view.findViewById(R.id.numberPicker1);
		nps.setDisplayedValues(city);
		nps.setMinValue(0);
		nps.setMaxValue(city.length - 1);
		nps.setFocusable(false);
		nps.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		nps.setOnValueChangedListener(new OnValueChangeListener() {
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

		npe = (NumberPicker) view.findViewById(R.id.numberPicker2);
		npe.setDisplayedValues(array);
		npe.setMinValue(0);
		npe.setMaxValue(array.length - 1);
		npe.setFocusable(false);
		npe.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		npe.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int oldVal, int newVal) {
				pickTime = time.get(newVal);
			}
		});
		return view;
	}
}