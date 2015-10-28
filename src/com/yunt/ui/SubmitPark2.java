package com.yunt.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.bepo.R;
import com.bepo.core.BaseAct;

public class SubmitPark2 extends BaseAct {

	static LinearLayout linBg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testssss);
		getTopBar("提交车位");

		linBg = (LinearLayout) this.findViewById(R.id.linBg);

	}

	public static void showBg() {
		linBg.setVisibility(View.VISIBLE);
	}

	public static void hideBg() {
		linBg.setVisibility(View.GONE);

	}
}
