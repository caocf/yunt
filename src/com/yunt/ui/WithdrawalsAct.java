package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.bean.ResultBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class WithdrawalsAct extends BaseAct {

	TextView tvTag, tvTag1, tvMoney, tvPayAccount, tvPayName, tvGetyanzhen;
	LinearLayout linPayInfo, linSubmit;
	RelativeLayout rlPayInfo;
	EditText etMoney, etYanzhenma;

	String WithdrawalsMoney;// ����Ǯ��
	String VerCode;// ��֤��
	String money = "";// ���
	ResultBean mResultBean;
	int recLen = 60;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.withdrawals);
		money = getIntent().getExtras().getString("money");
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		getPayInfo();
	}

	private void initView() {
		getTopBar("����");
		tvTag = (TextView) this.findViewById(R.id.tvTag);
		tvTag1 = (TextView) this.findViewById(R.id.tvTag1);
		tvMoney = (TextView) this.findViewById(R.id.tvMoney);
		tvMoney.setText(money);

		tvPayAccount = (TextView) this.findViewById(R.id.tvPayAccount);
		tvPayName = (TextView) this.findViewById(R.id.tvPayName);

		etMoney = (EditText) this.findViewById(R.id.etMoney);
		etYanzhenma = (EditText) this.findViewById(R.id.etYanzhenma);
		etMoney.addTextChangedListener(tw);
		etYanzhenma.addTextChangedListener(tw);

		rlPayInfo = (RelativeLayout) this.findViewById(R.id.rlPayInfo);
		rlPayInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(WithdrawalsAct.this, PayInfo4Withdrawals.class);
				intent.putExtra("money", money);
				startActivity(intent);
			}
		});
		tvGetyanzhen = (TextView) this.findViewById(R.id.tvGetyanzhen);
		tvGetyanzhen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				handler.postDelayed(runnable, 50);
				recLen = 60;
				handler.sendEmptyMessageDelayed(1, 60 * 1000);
				getAuthCode(PathConfig.userPhone);

			}
		});

		linPayInfo = (LinearLayout) this.findViewById(R.id.linPayInfo);
		linSubmit = (LinearLayout) this.findViewById(R.id.linSubmit);
		linSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String temp = etMoney.getText().toString().trim();
				if (MyTextUtils.isEmpty(temp)) {
					ToastUtils.showSuperToastAlert(WithdrawalsAct.this, "���ֽ���Ϊ��");
				} else {

					int temp1 = Integer.parseInt(temp);
					if (temp1 < 100) {
						ToastUtils.showSuperToastAlert(WithdrawalsAct.this, "�������������100");
					} else {
						WithdrawalsMoney = etMoney.getText().toString().trim();
						VerCode = etYanzhenma.getText().toString().trim();

						submitData();

					}
				}

			}
		});

	}

	// ==============================================================================
	// ==============================================================================
	private void getPayInfo() {
		String url = PathConfig.ADDRESS + "/trad/tradalipay/queryInfo?code=0";
		url = MyTextUtils.urlPlusAndFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				if (MyTextUtils.isEmpty(jsondata)) {
					tvTag.setVisibility(View.VISIBLE);
					tvTag1.setVisibility(View.VISIBLE);
					linPayInfo.setVisibility(View.GONE);
				} else {
					tvTag.setVisibility(View.GONE);
					linPayInfo.setVisibility(View.VISIBLE);
					tvTag1.setVisibility(View.GONE);
					Map<String, String> tempMap = JSON.parseObject(jsondata,
							new TypeReference<Map<String, String>>() {
							});

					tvPayAccount.setText(tempMap.get("ALIPAY_ACCOUNT"));
					tvPayName.setText(tempMap.get("ALIPAY_NAME"));
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
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
					ToastUtils.showSuperToastAlertGreen(WithdrawalsAct.this, "��֤������ɹ�,���Ե�!");
				} else {
					ToastUtils.showSuperToastAlertGreen(WithdrawalsAct.this, "��ȡ��֤��ʧ��,���Ժ�����!");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.showSuperToastAlertGreen(WithdrawalsAct.this, "���������ʧ��");
			}
		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	public void submitData() {
		showDialog();
		String url;
		url = PathConfig.ADDRESS + "/trad/tradwithd/add";
		url = MyTextUtils.urlPlusFoot(url);

		Map<String, String> params = new HashMap<String, String>();

		params.put("Money", WithdrawalsMoney);
		params.put("VerCode", VerCode);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(WithdrawalsAct.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(WithdrawalsAct.this, message.get("info"));
					finish();
				}
				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(WithdrawalsAct.this, "���ӷ�����ʧ��,���Ժ�����!");
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {

			if (60 - recLen < 59) {
				tvGetyanzhen.setClickable(false);
				recLen--;
				String strTime = "" + recLen;
				tvGetyanzhen.setBackgroundResource(R.drawable.gouwuche);
				tvGetyanzhen.setText(strTime + "��");
				handler.postDelayed(this, 1000);
			} else {
				tvGetyanzhen.setText(" �� ȡ ");
				tvGetyanzhen.setBackgroundResource(R.drawable.btn_gray_bg);
				handler.removeCallbacks(this);
				tvGetyanzhen.setClickable(true);

			}
		}
	};

	// edit����
	TextWatcher tw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {

			if (tvTag1.getVisibility() == View.VISIBLE) {
				linSubmit.setBackground(getResources().getDrawable(R.drawable.my_blue_btn_touming));
				linSubmit.setClickable(false);
			}

			if (MyTextUtils.isEmpty(etMoney.getText().toString())) {
				linSubmit.setBackground(getResources().getDrawable(R.drawable.my_blue_btn_touming));
				linSubmit.setClickable(false);
			} else if (MyTextUtils.isEmpty(etYanzhenma.getText().toString())) {
				linSubmit.setBackground(getResources().getDrawable(R.drawable.my_blue_btn_touming));
				linSubmit.setClickable(false);
			} else {
				linSubmit.setBackground(getResources().getDrawable(R.drawable.common_blue_btn));
				linSubmit.setClickable(true);
				linSubmit.setFocusable(true);
			}
		}
	};
}
