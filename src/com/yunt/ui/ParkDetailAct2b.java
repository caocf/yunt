package com.yunt.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

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

public class ParkDetailAct2b extends BaseAct implements OnClickListener {

	LinearLayout linCKtime, linCKmonth;
	CheckBox ckTime, ckMonth;
	LinearLayout linTime, linMonth;
	TextView tvOwnerName, tvBianHao, tvAddress, tvMonth;

	TextView tvXiaoqu;// 小区
	TextView tvMapAddress;// 描述地址
	TextView tvXingqi;// 星期
	TextView tvTime;// 时间
	TextView tvMoneyshi;// 时租
	TextView tvMoneyyue;// 月租
	TextView tvMoney;// 租金

	static TextView tvPlate;
	static TextView tvTimezu;
	RelativeLayout rlChoosePlate;
	RelativeLayout rlChooseTime;
	RelativeLayout rlSubmit;

	TimePicker timePicker;
	DatePicker datePicker;
	EditText etTime, etMonth;
	EditText etCount;
	ImageView ivJia, ivJian;
	TextView tvRentNumber;
	static TextView tvType;
	static TextView tvTypeName;
	static TextView tvNowPark;
	static TextView tvTotal;

	ImageView duihao0, duihao1;
	RelativeLayout rlDuihao0, rlDuihao1;

	String Code;// 车位code，车位拥有人名字，车位拥有人电话
	static String CodeRentType;// 时租月租切换标识
	static String BeginTime;// 起租时间
	static String RentNumber;// 租用数量
	static String PlateNumber;// 车牌号

	static Map<String, String> detailMap = new HashMap<String, String>();// 车位相关信息map
	Map<String, String> CurrentAccountMap = new HashMap<String, String>();// 车位拥有人map

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.park_detail_b);
		getTopBar("车位租用");
		Code = getIntent().getExtras().get("code").toString();
		initView();
		getData(Code);
		if (!PathConfig.clientkey.equals("")) {
			getPlate();
		}

	}

	private void getPlate() {
		String url = PathConfig.ADDRESS + "/base/buserplate/queryList?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) JSON.parseObject(
						jsondata, new TypeReference<ArrayList<HashMap<String, String>>>() {
						});
				HashMap<String, String> plateMap = new HashMap<String, String>();
				if (data.size() > 0) {

					for (HashMap<String, String> map : data) {
						if (map.get("SORT").equals("1")) {
							tvPlate.setText(map.get("PLATE1") + map.get("PLATE2") + map.get("PLATE3"));
							PlateNumber = map.get("PLATE1") + map.get("PLATE2") + map.get("PLATE3");
							return;
						}
					}

					ToastUtils.showSuperToastAlert(getApplicationContext(), "默认取出第一个");
					plateMap = data.get(0);
					tvPlate.setText(plateMap.get("PLATE1") + plateMap.get("PLATE2") + plateMap.get("PLATE3"));
					PlateNumber = plateMap.get("PLATE1") + plateMap.get("PLATE2") + plateMap.get("PLATE3");

					dismissDialog();

				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private void getData(String code) {
		String url = PathConfig.ADDRESS + "/base/bbusinesspark/info?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				// if (!tvPlate.getText().equals("请点击选择")) {
				dismissDialog();
				// }

				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				// 空闲时间
				tvNowPark.setText(detailMap.get("NOW_PARK") + "/" + detailMap.get("PARKS_NUM"));
				// 车场名称
				tvXiaoqu.setText(detailMap.get("CAR_PARK_NAME"));
				// 地址描述
				tvMapAddress.setText(detailMap.get("ADDRESS"));

				// 可租时段
				String week = detailMap.get("WEEKNAME");
				tvXingqi.setText(week);

				if (detailMap.get("ALL_TIME").equals("0")) {
					tvTime.setText(detailMap.get("START_TIME") + "-" + detailMap.get("END_TIME"));
				} else {
					tvTime.setText("全天可租");
				}

				tvMoneyshi.setText(detailMap.get("PRICE_HOUR") + "元/时");
				tvMoneyyue.setText(detailMap.get("PRICE_MONTH") + "元/月");

				tvMoney.setText(detailMap.get("PRICE_HOUR") + "元/时" + "  " + detailMap.get("PRICE_MONTH") + "元/月");

				tvType.setText(detailMap.get("PRICE_HOUR") + "元/时");
				tvTotal.setText(detailMap.get("PRICE_HOUR"));

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

		initDuihao();
		tvNowPark = (TextView) this.findViewById(R.id.tvNowPark);// 空闲车位
		tvXiaoqu = (TextView) this.findViewById(R.id.tvXiaoqu);// 车场名称
		tvMapAddress = (TextView) this.findViewById(R.id.tvMapAddress);// 描述地址
		tvXingqi = (TextView) this.findViewById(R.id.tvXingqi);// 星期
		tvTime = (TextView) this.findViewById(R.id.tvTime);// 时间
		tvMoneyshi = (TextView) this.findViewById(R.id.tvMoneyshi);// 时租金
		tvMoneyyue = (TextView) this.findViewById(R.id.tvMoneyyue);// 月租金
		tvMoney = (TextView) this.findViewById(R.id.tvMoney);

		tvPlate = (TextView) this.findViewById(R.id.tvPlate);// 车牌

		tvRentNumber = (TextView) this.findViewById(R.id.tvRentNumber);// 总价那里显示的数
		// 算式中的数量
		tvRentNumber.setText("×" + 1 + " =");

		tvTypeName = (TextView) this.findViewById(R.id.tvTypeName);// 灰框的时租月租
		tvTypeName.setText("时租");

		tvType = (TextView) this.findViewById(R.id.tvType);// 总价那里显示的类型
		tvTotal = (TextView) this.findViewById(R.id.tvTotal);// 总价那里显示的总数

		tvTimezu = (TextView) this.findViewById(R.id.tvTimezu);// 租赁起始时间
		// tvTimezu.setText(DateFormat.format("yyyy-MM-dd kk:mm",
		// Calendar.getInstance().getTime()).toString());
		// BeginTime = DateFormat.format("yyyy-MM-dd kk:mm",
		// Calendar.getInstance().getTime()).toString();

		etCount = (EditText) this.findViewById(R.id.etCount);

		// 数据初始化,数量和租赁类型 code
		etCount.setText("1");
		RentNumber = "1";
		CodeRentType = "1848";

		ivJia = (ImageView) this.findViewById(R.id.ivJia);
		ivJia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int jia = Integer.parseInt(etCount.getText().toString());
				jia++;
				etCount.setText(jia + "");
				RentNumber = jia + "";
				tvRentNumber.setText("×" + jia + " =");
				jisuanTotal();

			}
		});

		ivJian = (ImageView) this.findViewById(R.id.ivJian);
		ivJian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int jian = Integer.parseInt(etCount.getText().toString());
				jian--;
				if (jian == 0) {
					etCount.setText("1");
					RentNumber = "1";
					tvRentNumber.setText("×" + "1" + " =");
				} else {
					etCount.setText(jian + "");
					RentNumber = jian + "";
					tvRentNumber.setText("×" + jian + " =");
				}
				jisuanTotal();
			}
		});
		rlChoosePlate = (RelativeLayout) this.findViewById(R.id.rlChoosePlate);// 选择车牌
		rlChoosePlate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (!PathConfig.clientkey.equals("")) {
					Intent mIntent = new Intent(ParkDetailAct2b.this, ChoosePlate.class);
					mIntent.putExtra("isBusiness", "0");
					startActivity(mIntent);
				} else {
					Intent mIntent = new Intent(ParkDetailAct2b.this, LoginActivity.class);
					startActivity(mIntent);
				}

			}
		});

		rlChooseTime = (RelativeLayout) this.findViewById(R.id.rlChooseTime);// 选择时间
		rlChooseTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!PathConfig.clientkey.equals("")) {
					Intent mIntent = new Intent(ParkDetailAct2b.this, RentTime.class);
					mIntent.putExtra("isBusiness", "0");
					startActivity(mIntent);
				} else {
					Intent mIntent = new Intent(ParkDetailAct2b.this, LoginActivity.class);
					startActivity(mIntent);
				}

			}
		});

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);// 选择时间
		rlSubmit.setOnClickListener(this);
	}

	private void initDuihao() {
		duihao0 = (ImageView) this.findViewById(R.id.duihao0);
		duihao1 = (ImageView) this.findViewById(R.id.duihao1);
		duihao1.setVisibility(View.GONE);

		rlDuihao0 = (RelativeLayout) this.findViewById(R.id.rlDuihao0);
		rlDuihao0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				duihao0.setVisibility(View.VISIBLE);
				duihao1.setVisibility(View.GONE);
				setCodeRentType("1848");
			}
		});

		rlDuihao1 = (RelativeLayout) this.findViewById(R.id.rlDuihao1);
		rlDuihao1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				duihao0.setVisibility(View.GONE);
				duihao1.setVisibility(View.VISIBLE);
				setCodeRentType("1849");

			}
		});
	}

	protected static void jisuanTotal() {
		float total;

		if (CodeRentType.equals("1848")) {
			total = Float.parseFloat(detailMap.get("PRICE_HOUR")) * Integer.parseInt(RentNumber);
		} else {
			total = Float.parseFloat(detailMap.get("PRICE_MONTH")) * Integer.parseInt(RentNumber);
		}

		tvTotal.setText(total + "");
		// YoYo.with(Techniques.Shake).duration(200).playOn(findViewById(R.id.tvTotal));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlSubmit:
			if (MyTextUtils.isEmpty(PathConfig.clientkey)) {
				Intent mIntent = new Intent(ParkDetailAct2b.this, LoginActivity.class);
				startActivity(mIntent);
				finish();
			} else {
				submitData();
			}
			break;
		default:
			break;
		}

	}

	public static void setPlate(String s) {
		tvPlate.setText(s);
		PlateNumber = s;
	}

	public static void setCodeRentType(String s) {
		CodeRentType = s;
		if (s.equals("1848")) {
			tvType.setText(detailMap.get("PRICE_HOUR") + "元/时");
			tvTypeName.setText("时租");
		} else {
			tvType.setText(detailMap.get("PRICE_MONTH") + "元/月");
			tvTypeName.setText("月租");
		}
		jisuanTotal();
	}

	public static void setTimeZu(String s) {

		String ss = s.replace("点", "").replace("分", "");
		tvTimezu.setText(ss);
		BeginTime = s.replace("点", "").replace("分", "");
		jisuanTotal();
	}

	private void submitData() {
	
		if (MyTextUtils.isEmpty(BeginTime)) {
			ToastUtils.showSuperToastAlert(ParkDetailAct2b.this, "请选择租用开始时间");
			return;
		}
		if (MyTextUtils.isEmpty(PlateNumber)) {
			ToastUtils.showSuperToastAlert(ParkDetailAct2b.this, "请选择或输入您的车牌号");
			return;
		}
		showDialog();
		Log.e("canshu", "ReleaseParkCode:" + detailMap.get("CODE") + "," + "BeginTime:" + BeginTime + ","
				+ "CodeRentType:" + CodeRentType + ",RentNumber:" + RentNumber + ",PlateNumber:" + PlateNumber);

		String url = PathConfig.ADDRESS + "/trad/order/addSy?clientkey=" + PathConfig.clientkey;
		Map<String, String> params = new HashMap<String, String>();

		params.put("ReleaseParkCode", detailMap.get("CODE"));// 车位主键
		params.put("BeginTime", BeginTime);// 开始时间
		params.put("CodeRentType", CodeRentType);// 租赁类型 时租月租
		params.put("RentNumber", RentNumber);// 租赁数量
		params.put("PlateNumber", PlateNumber);// 租户车牌号

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(ParkDetailAct2b.this, "提交成功");
					Intent intent = new Intent(ParkDetailAct2b.this, CarPortOrderDetails.class);
					intent.putExtra("code", message.get("info"));
					startActivity(intent);
					finish();
				} else {
					ToastUtils.showSuperToastAlert(ParkDetailAct2b.this, message.get("info"));
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
}
