package com.dykj.trajectory;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class GPSDialog extends BaseAct {
	// private MyDialog dialog;
	private LinearLayout layout;
	private EditText etPhone;
	private View mLoading;
	private static String code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		mLoading = findViewById(R.id.loading);

		Intent intent = this.getIntent();
		code = intent.getStringExtra("code");

		etPhone = (EditText) findViewById(R.id.etPhone);

		layout = (LinearLayout) findViewById(R.id.exit_layout);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "tmac tmac tmac",
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return false;
	}

	public void exitbutton0(View v) {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
		this.finish();
	}

	public void exitbutton1(View v) {
		this.finish();

	}

}
