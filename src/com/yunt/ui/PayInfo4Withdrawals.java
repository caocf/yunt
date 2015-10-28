package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class PayInfo4Withdrawals extends BaseAct {
	LinearLayout linSubmit;
	EditText etPayName, etPayAccount;

	String code, AliPayName, AlipayAccount;
	Boolean isFirst = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payinfo_withdrawals);
		initView();
		getPayInfo();

	}

	private void initView() {
		getTopBar("收款账户");
		linSubmit = (LinearLayout) this.findViewById(R.id.linSubmit);
		linSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				AliPayName = etPayName.getText().toString().trim();
				AlipayAccount = etPayAccount.getText().toString().trim();
				if (MyTextUtils.isEmpty(AliPayName)) {
					ToastUtils.showSuperToastAlert(PayInfo4Withdrawals.this, "姓名不能为空");
					return;
				} else if (MyTextUtils.isEmpty(AlipayAccount)) {
					ToastUtils.showSuperToastAlert(PayInfo4Withdrawals.this, "账户不能为空");
					return;
				}
				submitData();

			}
		});
		etPayAccount = (EditText) this.findViewById(R.id.etPayAccount);
		etPayName = (EditText) this.findViewById(R.id.etPayName);
	}

	private void getPayInfo() {
		String url = PathConfig.ADDRESS + "/trad/tradalipay/queryInfo?code=0";
		url = MyTextUtils.urlPlusAndFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				if (!MyTextUtils.isEmpty(jsondata)) {
					isFirst = false;
					Map<String, String> tempMap = JSON.parseObject(jsondata,
							new TypeReference<Map<String, String>>() {
							});

					etPayAccount.setText(tempMap.get("ALIPAY_ACCOUNT"));
					AlipayAccount = tempMap.get("ALIPAY_ACCOUNT");

					etPayName.setText(tempMap.get("ALIPAY_NAME"));
					AliPayName = tempMap.get("ALIPAY_NAME");

					code = tempMap.get("CODE");
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	public void submitData() {
		showDialog();
		String url;

		Map<String, String> params = new HashMap<String, String>();

		params.put("AlipayName", AliPayName);
		params.put("AlipayAccount", AlipayAccount);
		if (isFirst) {
			url = PathConfig.ADDRESS + "/trad/tradalipay/add";
			url = MyTextUtils.urlPlusFoot(url);
		} else {
			params.put("Code", code);
			url = PathConfig.ADDRESS + "/trad/tradalipay/modify";
			url = MyTextUtils.urlPlusFoot(url);
		}

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(PayInfo4Withdrawals.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(PayInfo4Withdrawals.this, message.get("info"));
					finish();
				}
				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(PayInfo4Withdrawals.this, "连接服务器失败,请稍后重试!");
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);
	}

}
