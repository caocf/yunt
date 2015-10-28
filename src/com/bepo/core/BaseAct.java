package com.bepo.core;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.bepo.R;
import com.bepo.view.LodingPop;

public class BaseAct extends FragmentActivity {
	LinearLayout linBack;
	// TextView tvMid, tvRight;
	View v;
	private static LodingPop mLodingPop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.bottom_in, R.anim.hold);

		v = this.getLayoutInflater().inflate(R.layout.loading, null);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		layoutParams.setMargins(0, 20, 0, 0);
		getWindow().addContentView(v, layoutParams);
		// v.setVisibility(View.GONE);
	}

	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.bottom_out);
	}

	public void showDialog() {
		if (v != null) {
			v = this.getLayoutInflater().inflate(R.layout.loading, null);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			layoutParams.setMargins(0, 20, 0, 0);
			getWindow().addContentView(v, layoutParams);
		}

		v.setVisibility(View.VISIBLE);
	}

	public void dismissDialog() {
		if (v != null) {
			v.setVisibility(View.GONE);
		}

	}

	public void noBack(String arg0) {
		LinearLayout linBack = (LinearLayout) findViewById(R.id.linBack);
		linBack.setVisibility(View.GONE);
		TextView tvMid = (TextView) findViewById(R.id.tvMid);
		tvMid.setText(arg0);
	}

	public void getTopHasRight(String tit, String Right) {

		LinearLayout linBack = (LinearLayout) findViewById(R.id.linBack);
		linBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		TextView tvMid = (TextView) findViewById(R.id.tvMid);
		tvMid.setText(tit);

		TextView tvRight = (TextView) findViewById(R.id.tvRight);
		tvRight.setText(Right);
	}

	public void noBackHasRight(String tit, String Right) {

		LinearLayout linBack = (LinearLayout) findViewById(R.id.linBack);
		linBack.setVisibility(View.GONE);

		TextView tvMid = (TextView) findViewById(R.id.tvMid);
		tvMid.setText(tit);

		TextView tvRight = (TextView) findViewById(R.id.tvRight);
		tvRight.setText(Right);
	}

	public void getTopBar(String arg0) {
		TextView tvMid = (TextView) findViewById(R.id.tvMid);
		tvMid.setText(arg0);

		LinearLayout linBack = (LinearLayout) findViewById(R.id.linBack);
		linBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

	};

}
