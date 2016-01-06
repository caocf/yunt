package com.yunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.zxing.EPQRAct;

public class PaySucess extends BaseAct {

	TextView tvDingdan, tvErweima;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_sucess);
		getTopBar("Ö§¸¶³É¹¦");
		initView();
	}

	private void initView() {

		tvDingdan = (TextView) this.findViewById(R.id.tvDingdan);
		tvDingdan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent mIntent = new Intent(PaySucess.this, MyOrderList.class);
				startActivity(mIntent);
			}
		});

		tvErweima = (TextView) this.findViewById(R.id.tvErweima);
		tvErweima.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(PaySucess.this, EPQRAct.class);
				intent.putExtra("code", getIntent().getExtras().get("orderNumber").toString());
				startActivity(intent);
			}
		});
	}
}
