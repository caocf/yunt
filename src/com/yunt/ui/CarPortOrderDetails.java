package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.okhttp.OkHttpUtils;
import com.okhttp.StringCallback;
import com.squareup.okhttp.Request;

public class CarPortOrderDetails extends BaseAct {

	TextView tvOrderNumber;
	TextView tvOrderTotal;
	TextView tvPlate;
	TextView tvYue;
	public static TextView tvQuan;

	RelativeLayout rlYue, rlSubmit, rlQuan;
	ImageView ivduihao_yue;
	Boolean isUseYue = true;

	static LinearLayout linQuan;

	String code;
	static String chitCode = "";// 代金券码
	String PayPwd = "123456";// 余额支付密码
	Map<String, String> detailMap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carport_order_detail);
		getTopBar("确认订单");
		code = (String) getIntent().getExtras().get("code");
		initView();
		getData();

	}

	public static void dimissDilaog() {
		dimissDilaog();
	}

	private void initView() {

		ivduihao_yue = (ImageView) this.findViewById(R.id.ivduihao_yue);
		linQuan = (LinearLayout) this.findViewById(R.id.linQuan);
		linQuan.setVisibility(View.GONE);

		tvOrderNumber = (TextView) this.findViewById(R.id.tvOrderNumber);
		tvOrderTotal = (TextView) this.findViewById(R.id.tvOrderTotal);
		tvPlate = (TextView) this.findViewById(R.id.tvPlate);
		tvYue = (TextView) this.findViewById(R.id.tvYue);
		tvQuan = (TextView) this.findViewById(R.id.tvQuan);

		rlQuan = (RelativeLayout) this.findViewById(R.id.rlQuan);
		rlQuan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String temp = tvOrderTotal.getText().toString();
				temp = temp.replace("¥", "");
				float total = Float.parseFloat(temp);
				if (total >= 10) {
					Intent mIntent5 = new Intent(CarPortOrderDetails.this, VoucherList2Use.class);
					startActivity(mIntent5);
				} else {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "总金额超过十元方可使用");
				}

			}
		});

		rlYue = (RelativeLayout) this.findViewById(R.id.rlYue);
		rlYue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isUseYue) {
					ivduihao_yue.setVisibility(View.GONE);
					isUseYue = false;
					PayPwd = "";
				} else {
					ivduihao_yue.setVisibility(View.VISIBLE);
					isUseYue = true;
					PayPwd = "123456";
				}

			}
		});

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// String out_trade_no =
				// detailMap.get("ORDER_NUMBER").trim().toString();
				// String total_fee =
				// detailMap.get("PRICE_COUNT").trim().toString();
				// showDialog();
				// WXPayUtils mWXPayUtils = new WXPayUtils(out_trade_no,
				// total_fee);

				Log.e("pay==========", "orderCode=" + tvOrderNumber.getText().toString() + "  chitCode="
						+ chitCode + " Paypwd=" + PayPwd);
				showDialog();
				String url = PathConfig.ADDRESS + "/trad/order/payAdd";

				OkHttpUtils.post().url(url).addParams("clientkey", PathConfig.clientkey)
						.addParams("orderCode", code).addParams("chitCode", chitCode).addParams("PayPwd", PayPwd)
						.addParams("payType", "weChatPay").build().execute(new MyStringCallback());
			}
		});

	}

	public class MyStringCallback extends StringCallback {

		@Override
		public void onError(Request request, Exception e) {
			dismissDialog();
			ToastUtils.showSuperToastAlert(getApplicationContext(), "数据异常,提交失败,请稍后重试");
		}

		@Override
		public void onResponse(String response) {
			dismissDialog();
			// ToastUtils.showSuperToastAlert(getApplicationContext(),
			// response);

			HashMap<String, String> statusMap = (HashMap<String, String>) JSON.parseObject(response,
					new TypeReference<Map<String, String>>() {
					});

			if (statusMap.get("payFlag").equals("false")) {
				ToastUtils.showSuperToastAlertGreen(getApplicationContext(), "支付成功");
				finish();
			} else {

				String out_trade_no = detailMap.get("ORDER_NUMBER").trim().toString();
				String total_fee = detailMap.get("PRICE_COUNT").trim().toString();
				showDialog();
				WXPayUtils mWXPayUtils = new WXPayUtils(out_trade_no, total_fee);
			}

		}

	}

	private void getYue() {
		String url = PathConfig.ADDRESS + "/trad/tradcount/queryCount";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissDialog();
				String jsondata = response.toString();

				if (!MyTextUtils.isEmpty(jsondata)) {
					Map<String, String> tempMap = JSON.parseObject(jsondata,
							new TypeReference<Map<String, String>>() {
							});
					tvYue.setText(tempMap.get("MONEY").toString() + "元");
				} else {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "数据异常");
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	private void getData() {
		showDialog();
		String url = PathConfig.ADDRESS + "/trad/order/queryByCode?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				getYue();
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				tvOrderNumber.setText(detailMap.get("ORDER_NUMBER"));
				tvOrderTotal.setText("¥" + detailMap.get("PRICE_COUNT"));
				tvPlate.setText(detailMap.get("PLATE_NUMBER"));

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

}
