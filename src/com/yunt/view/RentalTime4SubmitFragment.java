package com.yunt.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.bepo.R;
import com.bepo.adapter.WeekAdapter;
import com.bepo.view.Time4PopBean;
import com.kyleduo.switchbutton.SwitchButton;

import de.greenrobot.event.EventBus;

public class RentalTime4SubmitFragment extends Fragment implements OnClickListener {

	public View view;
	GridView weekgridview;
	WeekAdapter weekAdapter;

	RelativeLayout rlStarTime, rlEndTime;
	LinearLayout linMain, linSettingTime;
	TimePop timePop;
	TextView tvStartTime, tvEndTime;
	private SwitchButton mSwitchButton;

	public static String allTime;
	public static ArrayList<String> week = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		view = inflater.inflate(R.layout.submit_rentaltime, container, false);
		weekgridview = (GridView) view.findViewById(R.id.weekgridview);

		rlStarTime = (RelativeLayout) view.findViewById(R.id.rlStarTime);
		rlStarTime.setOnClickListener(this);
		tvStartTime = (TextView) view.findViewById(R.id.tvStartTime);

		rlEndTime = (RelativeLayout) view.findViewById(R.id.rlEndTime);
		rlEndTime.setOnClickListener(this);
		tvEndTime = (TextView) view.findViewById(R.id.tvEndTime);

		linMain = (LinearLayout) view.findViewById(R.id.linMain);
		linSettingTime = (LinearLayout) view.findViewById(R.id.linSettingTime);

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

	public void onEventMainThread(Time4PopBean timeBean) {
		if (timeBean.getFlag().equals("0")) {
			tvStartTime.setText(timeBean.getTime());
		} else {
			tvEndTime.setText(timeBean.getTime());
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
		case R.id.rlStarTime:
			timePop = new TimePop(this.getActivity(), "0");
			timePop.showAtLocation(linMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.rlEndTime:
			timePop = new TimePop(this.getActivity(), "1");
			timePop.showAtLocation(linMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		default:
			break;
		}

	}
}
