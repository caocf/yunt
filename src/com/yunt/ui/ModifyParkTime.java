package com.yunt.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.core.BaseAct;

public class ModifyParkTime extends BaseAct {

	TextView tvCancle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
		setContentView(R.layout.modify_park_time);
		initView();

	}

	private void initView() {
		tvCancle = (TextView) this.findViewById(R.id.tvCancle);
		tvCancle.setText("ÐÞ¸Ä³µÎ»");
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

}
