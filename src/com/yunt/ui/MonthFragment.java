package com.yunt.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.RelativeLayout;

import com.bepo.R;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class MonthFragment extends Fragment {

	DatePicker dp;
	ArrayList<String> time;
	String date = "";
	RelativeLayout rlSubmit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.month_fra, container, false);

		rlSubmit = (RelativeLayout) view.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!date.equals("")) {

					if (RentTime.isBusiness) {
						ParkDetailAct2b.setTimeZu(date);
						ParkDetailAct2b.setCodeRentType("1849");
					} else {
						ParkDetailAct2.setTimeZu(date);
						ParkDetailAct2.setCodeRentType("1849");
					}

					getActivity().finish();

				} else {
					Calendar calendar = Calendar.getInstance();
					int year = calendar.get(Calendar.YEAR);
					int monthOfYear = calendar.get(Calendar.MONTH) + 1;
					int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
					String now = year + "-" + monthOfYear + "-" + dayOfMonth;

					if (RentTime.isBusiness) {
						ParkDetailAct2b.setTimeZu(now);
						ParkDetailAct2b.setCodeRentType("1849");
					} else {
						ParkDetailAct2.setTimeZu(now);
						ParkDetailAct2.setCodeRentType("1849");
					}

					getActivity().finish();
				}
			}
		});

		Calendar calendar = Calendar.getInstance();

		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		dp = (DatePicker) view.findViewById(R.id.datepicker);
		dp.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
			}

		});

		return view;
	}
}