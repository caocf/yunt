package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.bean.ResultBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.bepo.utils.SMSBroadcastReceiver;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class LoginActivity extends BaseAct implements OnClickListener {

	private LinearLayout linBack;
	private EditText etPhone;// 手机号
	private EditText etCode;// 验证码
	private TextView tvGetyanzhen;// 获取验证码
	private TextView tvNext;// 开始

	private String phone;
	private String yanzhen;
	ResultBean mResultBean;

	// save pwd
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private int recLen = 60;
	private int flag = 0;

	// 短信接收相关
	private SMSBroadcastReceiver mSMSBroadcastReceiver;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_zhuceopen);
		sp = getSharedPreferences("USER_INFO", MODE_PRIVATE);
		tvNext = (TextView) this.findViewById(R.id.tvNext);
		tvNext.setOnClickListener(this);

		linBack = (LinearLayout) this.findViewById(R.id.linBack);
		linBack.setOnClickListener(this);
		tvGetyanzhen = (TextView) this.findViewById(R.id.tvGetyanzhen);
		tvGetyanzhen.setOnClickListener(this);

		etPhone = (EditText) this.findViewById(R.id.etPhone);

	}

	@Override
	protected void onStart() {
		super.onStart();
		etCode = (EditText) this.findViewById(R.id.etCode);
		etCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (arg0.length() == 4 && flag == 1) {
					tvNext.setClickable(false);
					phone = etPhone.getText().toString().trim();
					yanzhen = etCode.getText().toString().trim();
					goHome(phone, yanzhen);
				}

			}
		});
		// 生成广播处理
		mSMSBroadcastReceiver = new SMSBroadcastReceiver();

		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter(ACTION);
		intentFilter.setPriority(Integer.MAX_VALUE);
		// 注册广播
		this.registerReceiver(mSMSBroadcastReceiver, intentFilter);

		mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
			@Override
			public void onReceived(String message) {
				flag = 1;
				etCode.setText(message.trim().split(":")[1]);
				// ToastUtils.showSuperToastAlert(getApplicationContext(),
				// message);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销短信监听广播
		this.unregisterReceiver(mSMSBroadcastReceiver);
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tvNext:
			showDialog();
			tvNext.setClickable(false);
			phone = etPhone.getText().toString().trim();
			yanzhen = etCode.getText().toString().trim();

			if (MyTextUtils.isEmpty(yanzhen)) {
				ToastUtils.toast(LoginActivity.this, "请输入验证码");
				tvNext.setClickable(true);
				return;
			}

			goHome(phone, yanzhen);

			break;

		case R.id.tvGetyanzhen:

			phone = etPhone.getText().toString().trim();

			if (MyTextUtils.isEmpty(phone)) {
				ToastUtils.toast(LoginActivity.this, "请输入手机号");
				return;
			}
			handler.postDelayed(runnable, 50);
			recLen = 60;
			handler.sendEmptyMessageDelayed(1, 60 * 1000);
			getAuthCode(phone);
			break;
		case R.id.linBack:
			LoginActivity.this.finish();
		}

	}

	private void goHome(final String phone, String yanzhen) {

		String url = PathConfig.ADDRESS + "/base/buser/app/action?phone=" + phone + "&number=" + yanzhen;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				String jsondata = response.toString();
				mResultBean = JSON.parseObject(jsondata, ResultBean.class);
				String info = mResultBean.getInfo().toString().trim();

				if (mResultBean.getStatus().equals("true")) {
					tvNext.setClickable(true);
					dismissDialog();
					Map<String, String> userMap = JSON.parseObject(info, new TypeReference<Map<String, String>>() {
					});
					sp.edit().putString("clientkey", userMap.get("clientkey")).commit();// 记住密码所用
					PathConfig.clientkey = userMap.get("clientkey");// 内存中缓存的clientkey

					if (JPushInterface.isPushStopped(getApplicationContext())) {
						JPushInterface.resumePush(getApplicationContext());
					}
					getCurrentAccountInfo();// 因为接口里边没有登录时返回相关信息的方法,这里只能通过
					// clientkey 再把推送需要的 Alias 值拿过来
					ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "欢迎回来");
					finish();

				} else {
					dismissDialog();
					tvNext.setClickable(true);
					ToastUtils.showSuperToastAlertGreen(LoginActivity.this, info);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				tvNext.setClickable(true);
				ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "连接服务器失败,请稍后重试!");
			}
		});

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(200* 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	private void getAuthCode(String phone) {

		String url = PathConfig.ADDRESS + "/base/buser/code/send?phone=" + phone;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				String jsondata = response.toString();
				mResultBean = JSON.parseObject(jsondata, ResultBean.class);

				if (mResultBean.getStatus().equals("true")) {
					String info = mResultBean.getInfo().toString().trim();
					ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "验证码请求成功,请稍等!");

				} else {
					ToastUtils.showSuperToastAlert(LoginActivity.this, "获取验证码失败,请稍后重试!");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				ToastUtils.showSuperToastAlert(LoginActivity.this, "连接服务器失败,请稍后重试!!");
			}
		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(200 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	// 通过session查询当前账户的相关信息,如果 carcode 不为空则允许提交，为空跳转车主信息完善页面
	private void getCurrentAccountInfo() {
		String url = PathConfig.ADDRESS + "/base/buser/queryBySessionCode?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				Map<String, String> CurrentAccountMap = new HashMap<String, String>();// session信息相关
				CurrentAccountMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				String Alias = CurrentAccountMap.get("CODE");
				JPushInterface.setAliasAndTags(getApplicationContext(), Alias, null, mAliasCallback);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			switch (code) {
			case 0:
				// MyTextUtils.showToast("登录成功", getApplicationContext());
				break;

			}

		}

	};

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {

			if (60 - recLen < 59) {
				tvGetyanzhen.setClickable(false);
				recLen--;
				String strTime = "" + recLen;
				tvGetyanzhen.setBackgroundResource(R.drawable.gouwuche);
				tvGetyanzhen.setText(strTime + "秒后可重新发送");
				handler.postDelayed(this, 1000);
			} else {
				tvGetyanzhen.setText("获取验证码");
				tvGetyanzhen.setBackgroundResource(R.drawable.btn_gray_bg);
				handler.removeCallbacks(this);
				tvGetyanzhen.setClickable(true);

			}
		}
	};
}