package com.yunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class PayPasswordDialog extends BaseAct {

	TextView tvCancel, tvYes, tvChang;
	EditText etPwd;
	LinearLayout linTishi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_password_dialog);
		getPwdStatus();

		linTishi = (LinearLayout) this.findViewById(R.id.linTishi);

		etPwd = (EditText) this.findViewById(R.id.etPwd);
		// 获取编辑框焦点
		etPwd.setFocusable(true);

		tvYes = (TextView) this.findViewById(R.id.tvYes);
		tvYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String pwd = etPwd.getText().toString().trim();
				if (MyTextUtils.isEmpty(pwd)) {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "密码不能为空");
					return;
				}

				if (pwd.length() < 6) {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "密码位数为6位");
					return;
				}
				CarPortOrderDetails.PayPwd = pwd;
				CarPortOrderDetails.ivduihao_yue.setVisibility(View.VISIBLE);
				finish();
			}
		});

		tvCancel = (TextView) this.findViewById(R.id.tvCancel);
		tvCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tvChang = (TextView) this.findViewById(R.id.tvChang);
		tvChang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent mIntent = new Intent(PayPasswordDialog.this, ChangPayPwd.class);
				startActivity(mIntent);

			}
		});

	}

	private void getPwdStatus() {

		String url = PathConfig.ADDRESS + "/base/buser/checkPwd";
		url = MyTextUtils.urlPlusFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				if (jsondata.equals("0")) {
					linTishi.setVisibility(View.VISIBLE);
				} else {
					linTishi.setVisibility(View.GONE);
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}
}
