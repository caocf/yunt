package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class LoginActivity extends BaseAct implements OnClickListener {

	private LinearLayout linBack;
	private EditText etPhone;// �ֻ���
	private EditText etCode;// ��֤��
	private TextView tvGetyanzhen;// ��ȡ��֤��
	private TextView tvNext;// ��ʼ

	private String phone;
	private String yanzhen;
	ResultBean mResultBean;

	// save pwd
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private int recLen = 60;

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
		etCode = (EditText) this.findViewById(R.id.etCode);

	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tvNext:
			showDialog();
			tvNext.setClickable(false);
			phone = etPhone.getText().toString().trim();
			yanzhen = etCode.getText().toString().trim();

			if (MyTextUtils.isEmpty(yanzhen)) {
				ToastUtils.toast(LoginActivity.this, "��������֤��");
				tvNext.setClickable(true);
				return;
			}

			goHome(phone, yanzhen);

			break;

		case R.id.tvGetyanzhen:

			phone = etPhone.getText().toString().trim();

			if (MyTextUtils.isEmpty(phone)) {
				ToastUtils.toast(LoginActivity.this, "�������ֻ���");
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
					sp.edit().putString("clientkey", userMap.get("clientkey")).commit();// ��ס��������
					PathConfig.clientkey = userMap.get("clientkey");// �ڴ��л����clientkey

					if (JPushInterface.isPushStopped(getApplicationContext())) {
						JPushInterface.resumePush(getApplicationContext());
					}
					getCurrentAccountInfo();// ��Ϊ�ӿ����û�е�¼ʱ���������Ϣ�ķ���,����ֻ��ͨ��
					// clientkey �ٰ�������Ҫ�� Alias ֵ�ù���
					ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "��ӭ����");
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
				ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "404!!");
			}
		});

		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
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
					ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "��֤������ɹ�,���Ե�!");

				} else {
					ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "��ȡ��֤��ʧ��,���Ժ�����!");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				ToastUtils.showSuperToastAlertGreen(LoginActivity.this, "404!!");
			}
		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	// ͨ��session��ѯ��ǰ�˻��������Ϣ,��� carcode ��Ϊ���������ύ��Ϊ����ת������Ϣ����ҳ��
	private void getCurrentAccountInfo() {
		String url = PathConfig.ADDRESS + "/base/buser/queryBySessionCode?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				Map<String, String> CurrentAccountMap = new HashMap<String, String>();// session��Ϣ���
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
//				MyTextUtils.showToast("��¼�ɹ�", getApplicationContext());
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
				tvGetyanzhen.setText(strTime + "�������·���");
				handler.postDelayed(this, 1000);
			} else {
				tvGetyanzhen.setText("��ȡ��֤��");
				tvGetyanzhen.setBackgroundResource(R.drawable.btn_gray_bg);
				handler.removeCallbacks(this);
				tvGetyanzhen.setClickable(true);

			}
		}
	};
}