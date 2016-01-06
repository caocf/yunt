package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.okhttp.OkHttpUtils;
import com.okhttp.StringCallback;
import com.squareup.okhttp.Request;

public class ChangPayPwd extends BaseAct {

	EditText etOldPwd, etNewPwd1, etNewPwd2;
	RelativeLayout rlSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chang_pay_pwd);
		getTopBar("修改余额密码");
		initView();

	}

	private void initView() {

		etOldPwd = (EditText) this.findViewById(R.id.etOldPwd);
		etNewPwd1 = (EditText) this.findViewById(R.id.etNewPwd1);
		etNewPwd2 = (EditText) this.findViewById(R.id.etNewPwd2);
		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				changPwd();
			}
		});
	}

	private void changPwd() {
		if (MyTextUtils.isEmpty(etOldPwd.getText().toString())) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "原始密码不能为空");
			return;
		}

		if (MyTextUtils.isEmpty(etNewPwd1.getText().toString())) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "新密码不能为空");
			return;
		}

		if (MyTextUtils.isEmpty(etNewPwd2.getText().toString())) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "新密码不能为空");
			return;
		}

		if (!etNewPwd1.getText().toString().equals(etNewPwd2.getText().toString())) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "两次输入的新密码不一致");
			return;
		}

		String url = PathConfig.ADDRESS + "/base/buser/modifyPayPwd";
		OkHttpUtils.post().url(url).addParams("clientkey", PathConfig.clientkey)
				.addParams("oldPwd", etOldPwd.getText().toString())
				.addParams("PayPwd", etNewPwd1.getText().toString()).build().execute(new MyStringCallback());

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

			HashMap<String, String> statusMap = (HashMap<String, String>) JSON.parseObject(response,
					new TypeReference<Map<String, String>>() {
					});

			if (statusMap.get("status").equals("true")) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), statusMap.get("info"));
				finish();
			} else {
				ToastUtils.showSuperToastAlert(getApplicationContext(), statusMap.get("info"));
			}

		}
	}
}
