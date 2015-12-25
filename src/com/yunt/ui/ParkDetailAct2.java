package com.yunt.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
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

public class ParkDetailAct2 extends BaseAct implements OnClickListener {

	LinearLayout linCKtime, linCKmonth;
	CheckBox ckTime, ckMonth;
	LinearLayout linTime, linMonth;
	TextView tvOwnerName, tvBianHao, tvAddress, tvMonth;

	TextView tvXiaoqu;// С��
	TextView tvMapAddress;// ������ַ
	TextView tvCheWei;// ��λ
	TextView tvXingqi;// ����
	TextView tvTime;// ʱ��
	TextView tvMoney;// ���
	TextView tvOwnerPhone;// �绰
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
	static TextView tvTotal;

	String Code;// ��λcode����λӵ�������֣���λӵ���˵绰
	static String CodeRentType;// ʱ�������л���ʶ
	static String BeginTime;// ����ʱ��
	static String RentNumber;// ��������
	static String PlateNumber;// ���ƺ�

	static Map<String, String> detailMap = new HashMap<String, String>();// ��λ�����Ϣmap
	Map<String, String> CurrentAccountMap = new HashMap<String, String>();// ��λӵ����map

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.park_detail);
		getTopBar("��λ����");
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

					ToastUtils.showSuperToastAlert(getApplicationContext(), "Ĭ��ȡ����һ��");
					plateMap = data.get(0);
					tvPlate.setText(plateMap.get("PLATE1") + plateMap.get("PLATE2") + plateMap.get("PLATE3"));
					PlateNumber = plateMap.get("PLATE1") + plateMap.get("PLATE2") + plateMap.get("PLATE3");

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
		String url = PathConfig.ADDRESS + "/base/breleasepark/info?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissDialog();
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				// С��
				tvXiaoqu.setText(detailMap.get("PARK_ADDRESS"));
				// ��ַ����
				tvMapAddress.setText(detailMap.get("ADDRESS"));
				// ��λ��źͳ��ƺ�
				if (detailMap.get("PLATE").length() > 1) {
					tvCheWei.setText(detailMap.get("PARK_NUMBER") + " " + detailMap.get("PLATE"));
				} else {
					tvCheWei.setText(detailMap.get("PARK_NUMBER"));
				}

				// ����ʱ��
				String week = detailMap.get("WEEKNAME");
				tvXingqi.setText(week);

				if (detailMap.get("ALL_TIME").equals("0")) {
					tvTime.setText(detailMap.get("START_TIME") + "-" + detailMap.get("END_TIME"));
				} else {
					tvTime.setText("ȫ�����");
				}

				tvMoney.setText(detailMap.get("PRICE_HOUR") + "Ԫ/ʱ" + "   " + detailMap.get("PRICE_MONTH") + "Ԫ/��");
				tvType.setText(detailMap.get("PRICE_HOUR") + "Ԫ/ʱ");
				tvTotal.setText(detailMap.get("PRICE_HOUR"));

				tvOwnerPhone.setText(detailMap.get("PARK_PHONE"));
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
		tvXiaoqu = (TextView) this.findViewById(R.id.tvXiaoqu);// С��
		tvMapAddress = (TextView) this.findViewById(R.id.tvMapAddress);// ������ַ
		tvCheWei = (TextView) this.findViewById(R.id.tvCheWei);// ��λ
		tvXingqi = (TextView) this.findViewById(R.id.tvXingqi);// ����
		tvTime = (TextView) this.findViewById(R.id.tvTime);// ʱ��
		tvMoney = (TextView) this.findViewById(R.id.tvMoney);// ���
		tvOwnerPhone = (TextView) this.findViewById(R.id.tvOwnerPhone);// �绰

		tvPlate = (TextView) this.findViewById(R.id.tvPlate);// ����

		tvRentNumber = (TextView) this.findViewById(R.id.tvRentNumber);// �ܼ�������ʾ����
		// ��ʽ�е�����
		tvRentNumber.setText("��" + 1 + " =");

		tvTypeName = (TextView) this.findViewById(R.id.tvTypeName);// �ҿ��ʱ������
		tvTypeName.setText("ʱ��");

		tvType = (TextView) this.findViewById(R.id.tvType);// �ܼ�������ʾ������
		tvTotal = (TextView) this.findViewById(R.id.tvTotal);// �ܼ�������ʾ������

		tvTimezu = (TextView) this.findViewById(R.id.tvTimezu);// ������ʼʱ��
		tvTimezu.setText(DateFormat.format("yyyy-MM-dd kk:mm", Calendar.getInstance().getTime()).toString());
		BeginTime = DateFormat.format("yyyy-MM-dd kk:mm", Calendar.getInstance().getTime()).toString();

		etCount = (EditText) this.findViewById(R.id.etCount);

		// ���ݳ�ʼ��,�������������� code
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
				tvRentNumber.setText("��" + jia + " =");
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
					tvRentNumber.setText("��" + "1" + " =");
				} else {
					etCount.setText(jian + "");
					RentNumber = jian + "";
					tvRentNumber.setText("��" + jian + " =");
				}
				jisuanTotal();
			}
		});
		rlChoosePlate = (RelativeLayout) this.findViewById(R.id.rlChoosePlate);// ѡ����
		rlChoosePlate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (!PathConfig.clientkey.equals("")) {
					Intent mIntent = new Intent(ParkDetailAct2.this, ChoosePlate.class);
					mIntent.putExtra("isBusiness", "");
					startActivity(mIntent);
				} else {
					Intent mIntent = new Intent(ParkDetailAct2.this, LoginActivity.class);
					startActivity(mIntent);
				}

			}
		});

		rlChooseTime = (RelativeLayout) this.findViewById(R.id.rlChooseTime);// ѡ��ʱ��
		rlChooseTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!PathConfig.clientkey.equals("")) {
					Intent mIntent = new Intent(ParkDetailAct2.this, RentTime.class);
					mIntent.putExtra("isBusiness", "");
					startActivity(mIntent);
				} else {
					Intent mIntent = new Intent(ParkDetailAct2.this, LoginActivity.class);
					startActivity(mIntent);
				}

			}
		});

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);// ѡ��ʱ��
		rlSubmit.setOnClickListener(this);
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
				Intent mIntent = new Intent(ParkDetailAct2.this, LoginActivity.class);
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
			tvType.setText(detailMap.get("PRICE_HOUR") + "Ԫ/ʱ");
			tvTypeName.setText("ʱ��");
		} else {
			tvType.setText(detailMap.get("PRICE_MONTH") + "Ԫ/��");
			tvTypeName.setText("����");
		}
		jisuanTotal();
	}

	public static void setTimeZu(String s) {
		tvTimezu.setText(s);
		BeginTime = s;
		jisuanTotal();
	}

	private void submitData() {
		showDialog();

		if (MyTextUtils.isEmpty(PlateNumber)) {
			ToastUtils.showSuperToastAlert(ParkDetailAct2.this, "��ѡ����������ĳ��ƺ�");
			return;
		}

		Log.e("canshu", "ReleaseParkCode:" + detailMap.get("CODE") + "," + "BeginTime:" + BeginTime + ","
				+ "CodeRentType:" + CodeRentType + ",RentNumber:" + RentNumber + ",PlateNumber:" + PlateNumber);

		String url = PathConfig.ADDRESS + "/trad/order/add?clientkey=" + PathConfig.clientkey;
		Map<String, String> params = new HashMap<String, String>();

		params.put("ReleaseParkCode", detailMap.get("CODE"));// ��λ����
		params.put("BeginTime", BeginTime);// ��ʼʱ��
		params.put("CodeRentType", CodeRentType);// �������� ʱ������
		params.put("RentNumber", RentNumber);// ��������
		params.put("PlateNumber", PlateNumber);// �⻧���ƺ�

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(ParkDetailAct2.this, "�ύ�ɹ�");
					Intent intent = new Intent(ParkDetailAct2.this, CarPortOrderDetails.class);
					intent.putExtra("code", message.get("info"));
					startActivity(intent);
					finish();
				} else {
					ToastUtils.showSuperToastAlert(ParkDetailAct2.this, message.get("info"));
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
}
