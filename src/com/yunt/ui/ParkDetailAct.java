package com.yunt.ui;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

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

public class ParkDetailAct extends BaseAct implements OnClickListener {

	LinearLayout linCKtime, linCKmonth;
	CheckBox ckTime, ckMonth;
	LinearLayout linTime, linMonth;
	TextView tvOwnerName, tvOwnerPhone, tvBianHao, tvAddress, tvTime, tvMonth, tvMoney;
	TimePicker timePicker;
	DatePicker datePicker;
	EditText etTime, etMonth;
	RelativeLayout rlSubmit;

	String Code;// 车位code，车位拥有人名字，车位拥有人电话
	String CodeRentType;// 时租月租切换标识
	String RentTime;// 起租时间
	String RentDay;// 起租日期
	String RentNumber;// 租用数量
	String total;// 租用价格

	Map<String, String> detailMap = new HashMap<String, String>();// 车位相关信息map
	Map<String, String> CurrentAccountMap = new HashMap<String, String>();// 车位拥有人map

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carport_detail);
		getTopBar("车位详情");
		Code = getIntent().getExtras().get("code").toString().split(",")[0];
		initView();
		initCheckBox();
		getData(Code);
	}

	// 通过session查询当前账户的相关信息,如果 carcode 不为空则允许提交，为空跳转车主信息完善页面
	private void getCurrentAccountInfo() {
		String url = PathConfig.ADDRESS + "/base/buser/queryBySessionCode?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				CurrentAccountMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (MyTextUtils.isEmpty(CurrentAccountMap.get("CARCODE"))) {

					Intent mIntent = new Intent(ParkDetailAct.this, SubmitCarOwnerInfo.class);
					startActivity(mIntent);

				} else {
					submitData();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	// 通过 code 查询此车位的详细信息
	private void getData(String code) {

		String url = PathConfig.ADDRESS + "/base/breleasepark/info?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissDialog();
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				tvOwnerName.setText(detailMap.get("PARK_NAME"));
				tvOwnerPhone.setText(detailMap.get("PARK_PHONE"));
				tvBianHao.setText(detailMap.get("PARK_NUMBER"));
				tvAddress.setText(detailMap.get("PARK_ADDRESS"));
				tvTime.setText(detailMap.get("PRICE_HOUR") + "元/小时");
				tvMonth.setText(detailMap.get("PRICE_MONTH") + "元/月");
				tvOwnerPhone.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
								+ detailMap.get("PARK_PHONE")));
						startActivity(intent);
					}
				});
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	private void initView() {

		showDialog();
		// 车位业主相关控件
		tvOwnerName = (TextView) this.findViewById(R.id.tvOwnerName);
		tvOwnerPhone = (TextView) this.findViewById(R.id.tvOwnerPhone);

		tvBianHao = (TextView) this.findViewById(R.id.tvBianHao);
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		tvTime = (TextView) this.findViewById(R.id.tvTime);
		tvMonth = (TextView) this.findViewById(R.id.tvMonth);

		tvMoney = (TextView) this.findViewById(R.id.tvMoney);
		tvMoney.setText("0元");

		timePicker = (TimePicker) this.findViewById(R.id.timepicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker arg0, int H, int M) {
				RentTime = H + ":" + M;

			}
		});

		datePicker = (DatePicker) this.findViewById(R.id.datepicker);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		datePicker.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {

			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			}

		});
		// 数量选择
		etTime = (EditText) this.findViewById(R.id.etTime);
		etMonth = (EditText) this.findViewById(R.id.etMonth);
		etTime.addTextChangedListener(timeTextWatcher);
		etMonth.addTextChangedListener(monthTextWatcher);

		// 时租月租切换模块
		linCKtime = (LinearLayout) this.findViewById(R.id.linCKtime);
		linCKmonth = (LinearLayout) this.findViewById(R.id.linCKmonth);
		linTime = (LinearLayout) this.findViewById(R.id.linTime);
		linMonth = (LinearLayout) this.findViewById(R.id.linMonth);

		ckTime = (CheckBox) this.findViewById(R.id.ckTime);
		ckTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean bool) {
				CodeRentType = "1848";
				tvMoney.setText("0元");
				if (bool) {
					ckMonth.setChecked(false);
					linMonth.setVisibility(View.GONE);
					linTime.setVisibility(View.VISIBLE);

				} else {
					linTime.setVisibility(View.GONE);
					linMonth.setVisibility(View.VISIBLE);
				}

			}
		});

		ckMonth = (CheckBox) this.findViewById(R.id.ckMonth);
		ckMonth.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean bool) {
				tvMoney.setText("0元");
				CodeRentType = "1849";
				if (bool) {
					ckTime.setChecked(false);
					linTime.setVisibility(View.GONE);
					linMonth.setVisibility(View.VISIBLE);

				} else {
					linMonth.setVisibility(View.GONE);
					linTime.setVisibility(View.VISIBLE);
				}

			}
		});

		// 提交按钮初始化
		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(this);
	}

	private void initCheckBox() {
		ckTime.setChecked(true);
		ckMonth.setChecked(false);
		linTime.setVisibility(View.VISIBLE);
		linMonth.setVisibility(View.GONE);
		CodeRentType = "1848";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlSubmit:
			if (MyTextUtils.isEmpty(PathConfig.clientkey)) {
				Intent mIntent = new Intent(ParkDetailAct.this, LoginActivity.class);
				startActivity(mIntent);
				finish();
			} else {
				getCurrentAccountInfo();
			}
			break;
		default:
			break;
		}

	}

	private void submitData() {
		showDialog();

		if (MyTextUtils.isEmpty(RentNumber)) {
			ToastUtils.showSuperToastAlert(this, "租用时间不能为空");
			dismissDialog();
			return;
		}

		if (MyTextUtils.isEmpty(RentTime)) {
			RentTime = DateFormat.format("yyyy-MM-dd kk:mm", Calendar.getInstance().getTime()).toString();
		}

		if (MyTextUtils.isEmpty(RentDay)) {
			RentDay = DateFormat.format("kk:mm", Calendar.getInstance().getTime()).toString();
		}

		String url = PathConfig.ADDRESS + "/trad/order/add?clientkey=" + PathConfig.clientkey;
		Map<String, String> params = new HashMap<String, String>();

		params.put("ReleaseParkCode", detailMap.get("CODE"));
		params.put("ParkOwnerCode", detailMap.get("CREATED_BY"));
		params.put("PriceHour", detailMap.get("PRICE_HOUR"));
		params.put("PriceMonth", detailMap.get("PRICE_MONTH"));
		params.put("BeginTime", CodeRentType.equals("1848") ? RentTime : RentDay);
		params.put("CodeRentType", CodeRentType);
		params.put("RentNumber", RentNumber);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(ParkDetailAct.this, "提交成功");
					Intent intent = new Intent(ParkDetailAct.this, CarPortOrderDetails.class);
					intent.putExtra("code", message.get("info"));
					startActivity(intent);
					finish();
				} else {
					ToastUtils.showSuperToastAlert(ParkDetailAct.this, message.get("info"));
					finish();
				}

				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);

	}

	TextWatcher timeTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			int i = Integer.parseInt(s.toString());
			float hours = Float.parseFloat(detailMap.get("PRICE_HOUR"));

			String temp = DateFormat.format("MM-dd ", Calendar.getInstance().getTime()).toString();
			RentNumber = i + "";
			tvMoney.setText(hours * i + "");
		}

		@Override
		public void afterTextChanged(Editable s) {

		};

	};

	TextWatcher monthTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			int i = Integer.parseInt(s.toString());
			float month = Float.parseFloat(detailMap.get("PRICE_MONTH"));
			RentNumber = i + "";
			tvMoney.setText(month * i + "");
		}

		@Override
		public void afterTextChanged(Editable s) {

		};

	};

}
