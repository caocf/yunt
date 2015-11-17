package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.photo.Bimp;
import com.bepo.utils.CameraUtil;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.view.BasicInfo4SubmitFragment;
import com.yunt.view.RentalInfo4SubmitFragment;
import com.yunt.view.RentalTime4SubmitFragment;
import com.yunt.view.SubmitParkSucess;

/**
 * 
 * @author kefanbufan
 * @date 2015��11��11��15:58:30
 * @�����ύģʽΪ ���ύͼƬ,�ɹ����ύ��������,�ɹ����� parkcode ,ͨ�� parkcode Ϊ�����ύ���ں�ʱ��
 * 
 */

public class ModifyPark extends BaseAct implements OnClickListener {

	LinearLayout linBg, linFragment, linStep;
	RelativeLayout rlSubmit, rlZujin, rlTime;
	public TextView tvCancle;
	public TextView tvMapAddress;// ��ͼ��λ
	public TextView tvXiaoqu;// С��
	public TextView tvChewei;// ��λ
	public TextView tvMenjin;// �Ž�
	public TextView tvPlate;// ����
	public TextView tvPriceHour;
	public TextView tvPriceMonth;
	public TextView tvXingqi;
	public TextView tvTime;
	public TextView tvPhone;

	// 1.������Ϣ�ֶ�
	public static String Address, positionX, positionY, ParkAddress, CarParkCode, ParkNumber, Plate, CodePosition,
			Garage;

	// 2.�Ž������ֶ�
	public static String CodeControlType, HasParkControl, ParkImg, Remarks;

	// 3.ʱ��۸��ֶ�
	// �۸�
	public static String PriceHour = "";
	public static String PriceMonth = "";
	// ʱ��
	public static String allTime;
	public static String startTime = "";
	public static String endTime = "";
	// ��������
	public static String week = "";
	int dateFlag = 1;

	// ͼƬ
	ArrayList<String> list = new ArrayList<String>();
	private Bitmap myBitmap;
	private byte[] myByte;
	private int picFlag = 0;

	// ��ȡ����������Ϣ
	String code;
	public HashMap<String, String> detailMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_park);
		code = getIntent().getExtras().getString("code");
		initView();
		getParkInfo();

	}

	private void initView() {

		tvCancle = (TextView) this.findViewById(R.id.tvCancle);
		tvCancle.setText("ȡ��");
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(this);

		tvMapAddress = (TextView) this.findViewById(R.id.tvMapAddress);
		tvXiaoqu = (TextView) this.findViewById(R.id.tvXiaoqu);
		tvChewei = (TextView) this.findViewById(R.id.tvCheWei);
		tvMenjin = (TextView) this.findViewById(R.id.tvMenjin);
		tvPlate = (TextView) this.findViewById(R.id.tvPlate);
		tvPriceHour = (TextView) this.findViewById(R.id.tvPriceHour);
		tvPriceMonth = (TextView) this.findViewById(R.id.tvPriceMonth);
		tvXingqi = (TextView) this.findViewById(R.id.tvXingqi);
		tvTime = (TextView) this.findViewById(R.id.tvTime);
		tvPhone = (TextView) this.findViewById(R.id.tvPhone);

		rlZujin = (RelativeLayout) this.findViewById(R.id.rlZujin);
		rlZujin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(ModifyPark.this, ModifyParkJiaGe.class);
				intent.putExtra("code", detailMap.get("CODE").toString());
				ModifyPark.this.startActivity(intent);

			}
		});

		rlTime = (RelativeLayout) this.findViewById(R.id.rlTime);
		rlTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent2 = new Intent(ModifyPark.this, ModifyParkTime.class);
				intent2.putExtra("code", detailMap.get("CODE").toString());
				ModifyPark.this.startActivity(intent2);

			}
		});

	}

	private void getParkInfo() {

		String url = PathConfig.ADDRESS + "/base/breleasepark/info?code=" + code;
		url = MyTextUtils.urlPlusAndFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<HashMap<String, String>>() {

				});

				tvMapAddress.setText(detailMap.get("ADDRESS"));
				tvXiaoqu.setText(detailMap.get("PARK_ADDRESS"));
				if (detailMap.get("GARAGE") != null) {
					tvChewei.setText(detailMap.get("GARAGE") + detailMap.get("PARK_NUMBER") + "("
							+ detailMap.get("CODE_POSITION_NAME") + ")");
				} else {
					tvChewei.setText(detailMap.get("PARK_NUMBER") + " (" + detailMap.get("CODE_POSITION_NAME")
							+ ")");
				}

				if (detailMap.get("HAS_PARK_CONTROL").equals("0")) {
					tvMenjin.setText(detailMap.get("CODE_CONTROL_TYPE_NAME"));
				} else {
					tvMenjin.setText("С���Ž�Ϊ" + detailMap.get("CODE_CONTROL_TYPE_NAME") + ",�������Ž�");
				}

				tvPlate.setText(detailMap.get("PLATE"));
				tvPriceHour.setText(detailMap.get("PRICE_HOUR") + "Ԫ/ʱ");
				tvPriceMonth.setText(detailMap.get("PRICE_MONTH") + "Ԫ/��");
				tvXingqi.setText(detailMap.get("WEEKNAME"));
				tvPhone.setText(detailMap.get("PARK_PHONE"));

				if (detailMap.get("ALL_TIME").equals("0")) {
					tvTime.setText(detailMap.get("START_TIME") + "-" + (detailMap.get("END_TIME")));
				} else {
					tvTime.setText("ȫ�����");
				}

				// positionX = detailMap.get("POSITION_X");
				// positionY = detailMap.get("POSITION_Y");

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlSubmit:
			getBascinfo();
			getRentalInfo();
			getRentalTime();
		}
	}

	// �õ�������Ϣ������
	@SuppressWarnings("unchecked")
	private void getBascinfo() {
		// TODO

		// ��ͼ��λ
		TextView temp = (TextView) this.findViewById(R.id.tvMapAddress);
		Address = (String) temp.getText();

		// ��γ��
		HashMap<String, String> map = new HashMap<String, String>();
		map = (HashMap<String, String>) temp.getTag();
		positionX = map.get("positionX");
		positionY = map.get("positionY");
		if (MyTextUtils.isEmpty(positionX)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "��ͼ��λ����Ϊ��");
			return;
		}

		// С��ȫ��
		TextView etXiaoqu = (TextView) this.findViewById(R.id.etXiaoqu);
		ParkAddress = etXiaoqu.getText().toString();
		if (MyTextUtils.isEmpty(ParkAddress)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "С��ȫ�Ʋ���Ϊ��");
			return;
		}
		// С��code
		CarParkCode = BasicInfo4SubmitFragment.CarParkCode;

		// ��������
		CodePosition = BasicInfo4SubmitFragment.CodePosition;
		EditText etDikuName = (EditText) this.findViewById(R.id.etDikuName);
		Garage = etDikuName.getText().toString();

		// ��λ���
		EditText etBianhao = (EditText) this.findViewById(R.id.etBianhao);
		ParkNumber = etBianhao.getText().toString();

		// ҵ������
		TextView tvjc = (TextView) this.findViewById(R.id.tvJiancheng);
		EditText etCarNumber = (EditText) this.findViewById(R.id.etCarNumber);
		Plate = tvjc.getText() + etCarNumber.getText().toString();

		if (MyTextUtils.isEmpty(ParkNumber) && MyTextUtils.isEmpty(etCarNumber.getText().toString())) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "��λ��ź�ҵ�����Ʋ���ͬʱΪ��");
			return;
		}

		Log.e("�ύ��λ_������Ϣ", Address + " " + positionX + "," + positionY + " " + "С��ȫ��:" + ParkAddress + " "
				+ "С��code:" + CarParkCode + " " + "��������:" + CodePosition + " " + "��������:" + Garage + " " + "��λ���:"
				+ ParkNumber + " " + Plate);

	}

	private void getRentalInfo() {
		// ���������Ž�
		HasParkControl = RentalInfo4SubmitFragment.HasParkControl;

		// �Ž�����
		CodeControlType = RentalInfo4SubmitFragment.CodeControlType;

		// ��ע
		EditText etRemarks = (EditText) this.findViewById(R.id.etRemarks);
		Remarks = etRemarks.getText().toString().trim();

		Log.e("�ύ��λ_�Ž�����", "�����Ž�:" + HasParkControl + " " + "С���Ž�:" + CodeControlType + " " + "��ע:" + Remarks);

	}

	private void getRentalTime() {

		// ����۸�
		EditText etPriceHour = (EditText) this.findViewById(R.id.etPriceHour);
		PriceHour = etPriceHour.getText().toString().trim();
		if (MyTextUtils.isEmpty(PriceHour)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "����۸���Ϊ��");
			return;
		}

		// ����۸�
		EditText etPriceMonth = (EditText) this.findViewById(R.id.etPriceMonth);
		PriceMonth = etPriceMonth.getText().toString().trim();
		if (MyTextUtils.isEmpty(PriceMonth)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "����۸���Ϊ��");
			return;
		}

		// 24Сʱ����
		allTime = RentalTime4SubmitFragment.allTime;

		// ��ʼʱ��
		TextView tvstartTime = (TextView) this.findViewById(R.id.tvStartTime);
		startTime = tvstartTime.getText().toString().trim();
		// ����ʱ��
		TextView tvEndTime = (TextView) this.findViewById(R.id.tvEndTime);
		endTime = tvEndTime.getText().toString().trim();

		// ��ȫ�����ʱ�ķǿ��ж�
		if (allTime.equals("0")) {
			if (startTime.equals("���ѡ��")) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "��ʼʱ�䲻��Ϊ��");
				return;
			}
			if (endTime.equals("���ѡ��")) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "����ʱ�䲻��Ϊ��");
				return;
			}
			// �ж�ʱ����
			int s = Integer.parseInt(startTime.replace(":", "").trim());
			int e = Integer.parseInt(endTime.replace(":", "").trim());
			if (s - e >= 0) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "��ʼʱ�䲻�ܴ��ڽ���ʱ��");
				return;
			}
		} else {
			startTime = "0";
			endTime = "0";
		}

		// ��������
		for (String temp : RentalTime4SubmitFragment.week) {
			week = week + "," + temp;
		}
		if (!week.isEmpty()) {
			week = week.substring(1);
		} else {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "�������ڲ���Ϊ��");
			return;
		}

		Log.e("�ύ��λ_ʱ��۸�", " ����۸�:" + PriceHour + " " + "����۸�:" + PriceMonth + " " + "��ʼʱ��:" + startTime + " "
				+ "������ʱ��:" + endTime + " " + " 24Сʱ����:" + allTime + " " + " ��������:" + week);

		// �ύ��ַ
		showDialog();
		if (Bimp.tempSelectBitmap.size() > 0) {

			for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
				myBitmap = Bimp.tempSelectBitmap.get(i).getBitmap();
				myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
				list.add(CameraUtil.byte2hex(myByte));
			}

			for (int i = 0; i < list.size(); i++) {
				submitPic(list.get(i));
			}

		} else {
			submitData();
		}

	}

	private void submitPic(String s) {

		String picUrl = PathConfig.ADDRESS + "/zcw/base/bupload/uploadApp";
		picUrl = MyTextUtils.urlPlusFoot(picUrl);
		Map<String, String> params = new HashMap<String, String>();
		params.put("picString", s);
		params.put("inCode", ParkImg);

		Request<JSONObject> request = new VolleyCommonPost(picUrl, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (picFlag <= Bimp.tempSelectBitmap.size()) {
					picFlag++;
				} else {
					submitData();
				}

				// ToastUtils.showSuperToastAlert(getApplication(), jsondata);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "�ϴ�ͼƬʧ��");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);
	}

	protected void submitData() {

		String url;

		// if (tvlogin.getText().equals("ȷ���޸�")) {
		// url = PathConfig.ADDRESS + "/base/breleasepark/modify";
		// url = MyTextUtils.urlPlusFoot(url);
		// } else {
		url = PathConfig.ADDRESS + "/base/breleasepark/add";
		url = MyTextUtils.urlPlusFoot(url);
		// }

		Map<String, String> params = new HashMap<String, String>();
		params.put("Address", Address);
		params.put("positionX", positionX);
		params.put("positionY", positionY);
		params.put("ParkAddress", ParkAddress);
		params.put("CarParkCode", CarParkCode);
		params.put("ParkNumber", ParkNumber);
		params.put("Plate", Plate);
		params.put("CodePosition", CodePosition);
		params.put("Garage", Garage);

		params.put("CodeControlType", CodeControlType);
		params.put("HasParkControl", HasParkControl);
		params.put("ParkImg", ParkImg);
		params.put("Remarks", Remarks);

		params.put("PriceHour", PriceHour);
		params.put("PriceMonth", PriceMonth);
		params.put("allTime", allTime);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("week", week);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					dismissDialog();
					finish();
					Intent intent03 = new Intent(ModifyPark.this, SubmitParkSucess.class);
					startActivity(intent03);
				} else {
					ToastUtils.showSuperToastAlert(ModifyPark.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(ModifyPark.this, "���ӷ�����ʧ��,���Ժ�����!");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(200 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);
	}

	protected void submitDate(String parkCode) {
		String url;
		url = PathConfig.ADDRESS + "/base/bwake/add";
		url = MyTextUtils.urlPlusFoot(url);

		Map<String, String> params = new HashMap<String, String>();
		params.put("parkCode", parkCode);
		params.put("week", week);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("allTime", allTime);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				dismissDialog();
				if (message.get("status").equals("true")) {
					finish();
					Intent intent03 = new Intent(ModifyPark.this, SubmitParkSucess.class);
					startActivity(intent03);

				} else {
					ToastUtils.showSuperToastAlert(ModifyPark.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(ModifyPark.this, "���ӷ�����ʧ��,���Ժ�����!");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);

	}

}
