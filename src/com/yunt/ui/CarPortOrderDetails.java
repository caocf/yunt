package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.pay.WXPayUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class CarPortOrderDetails extends BaseAct {

	TextView tvOrderNumber;
	TextView tvOrderTotal;
	RelativeLayout rlSubmit, rlZhifubao;

	String code;
	Map<String, String> detailMap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carport_order_detail);
		getTopBar("确认订单");
		code = (String) getIntent().getExtras().get("code");
		getData();
		initView();

	}

	public static void dimissDilaog() {
		dimissDilaog();
	}

	private void initView() {
		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String out_trade_no = detailMap.get("ORDER_NUMBER").trim().toString();
				String total_fee = detailMap.get("PRICE_COUNT").trim().toString();
				showDialog();
				WXPayUtils mWXPayUtils = new WXPayUtils(out_trade_no, total_fee);

			}
		});

		rlZhifubao = (RelativeLayout) this.findViewById(R.id.rlZhifubao);
		rlZhifubao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ToastUtils.showSuperToastComment(CarPortOrderDetails.this, "支付宝支付功能阿里巴巴也在审核中,敬请期待!");
			}
		});
		tvOrderNumber = (TextView) this.findViewById(R.id.tvOrderNumber);
		tvOrderTotal = (TextView) this.findViewById(R.id.tvOrderTotal);

	}

	private void getData() {
		showDialog();
		String url = PathConfig.ADDRESS + "/trad/order/queryByCode?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissDialog();
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				tvOrderNumber.setText(detailMap.get("ORDER_NUMBER"));
				tvOrderTotal.setText("¥" + detailMap.get("PRICE_COUNT"));

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

}
