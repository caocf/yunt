package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

public class SubmitPark2 extends BaseAct implements OnClickListener {

	static LinearLayout linBg, linFragment, linStep;
	RelativeLayout rlSubmit;
	ImageView ivStep01, ivStep02, ivStep03, ivStep04;
	ImageView ivLine02, ivLine03;
	TextView tvStep01, tvStep02, tvStep03, tvStep04;
	TextView tvCancle, tvSubmit;

	BasicInfo4SubmitFragment mBasicInfo4SubmitFragment;
	RentalInfo4SubmitFragment mRentalInfo4SubmitFragment;
	RentalTime4SubmitFragment mRentalTime4SubmitFragment;

	int flag;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testssss);
		// ��ȡ��������
		getPicCode();
		initView();

	}

	private void initView() {
		linBg = (LinearLayout) this.findViewById(R.id.linBg);
		linStep = (LinearLayout) this.findViewById(R.id.linStep);

		ivStep02 = (ImageView) this.findViewById(R.id.ivStep02);
		ivStep01 = (ImageView) this.findViewById(R.id.ivStep01);
		ivStep03 = (ImageView) this.findViewById(R.id.ivStep03);

		ivLine02 = (ImageView) this.findViewById(R.id.ivLine02);
		ivLine03 = (ImageView) this.findViewById(R.id.ivLine03);

		tvStep01 = (TextView) this.findViewById(R.id.tvStep01);
		tvStep02 = (TextView) this.findViewById(R.id.tvStep02);
		tvStep03 = (TextView) this.findViewById(R.id.tvStep03);

		tvSubmit = (TextView) this.findViewById(R.id.tvSubmit);

		tvCancle = (TextView) this.findViewById(R.id.tvCancle);
		tvCancle.setText("ȡ��");
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				switch (flag) {

				case 1:
					finish();
					break;

				case 2:
					resetFragment();
					transaction.show(mBasicInfo4SubmitFragment);
					transaction.commit();
					flag = 1;
					jumpStep();
					tvCancle.setText("ȡ��");
					break;

				case 3:
					resetFragment();
					transaction.show(mRentalInfo4SubmitFragment);
					transaction.commit();
					flag = 2;
					jumpStep();
					break;

				default:
					break;
				}
			}
		});

		linFragment = (LinearLayout) this.findViewById(R.id.linFragment);

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(this);

		setDefaultFragment();
		flag = 1;

	}

	private void setDefaultFragment() {

		mBasicInfo4SubmitFragment = new BasicInfo4SubmitFragment();
		mRentalInfo4SubmitFragment = new RentalInfo4SubmitFragment();
		mRentalTime4SubmitFragment = new RentalTime4SubmitFragment();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.add(R.id.linFragment, mBasicInfo4SubmitFragment);
		transaction.add(R.id.linFragment, mRentalInfo4SubmitFragment);
		transaction.add(R.id.linFragment, mRentalTime4SubmitFragment);

		transaction.hide(mRentalInfo4SubmitFragment);
		transaction.hide(mRentalTime4SubmitFragment);
		transaction.commit();
	}

	void whatCancel() {
		switch (flag) {
		case 1:
			tvCancle.setText("ȡ��");
			break;
		default:
			tvCancle.setText("��һ��");
			break;

		}

	}

	private void switchFrament() {
		resetFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.fragment_pop_left_enter, R.anim.fragment_left_exit,
				R.anim.fragment_pop_left_enter, R.anim.fragment_pop_left_exit);
		switch (flag) {

		case 1:
			transaction.show(mBasicInfo4SubmitFragment);
			transaction.commit();
			break;
		case 2:
			transaction.show(mRentalInfo4SubmitFragment);
			transaction.commit();
			break;
		case 3:
			transaction.show(mRentalTime4SubmitFragment);
			transaction.commit();
			break;

		}

	}

	private void resetFragment() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.hide(mBasicInfo4SubmitFragment);
		transaction.hide(mRentalInfo4SubmitFragment);
		transaction.hide(mRentalTime4SubmitFragment);
		// transaction.hide(mOtherInfo4SubmitFragment);
		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlSubmit:
			switch (flag) {

			// ���Ի���
			// flag = flag + 1;
			// jumpStep();
			// switchFrament();

			// ��������
			case 1:
				getBascinfo();
				break;
			case 2:
				getRentalInfo();
				break;

			case 3:
				getRentalTime();
				break;

			}

		default:
			break;
		}
	}

	// �õ�������Ϣ������
	@SuppressWarnings("unchecked")
	private void getBascinfo() {

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

		flag = 2;
		jumpStep();
		switchFrament();
		whatCancel();
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

		flag = 3;
		jumpStep();
		switchFrament();
		whatCancel();
		tvSubmit.setText("ȷ���ύ");
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

	private void getPicCode() {

		String url = PathConfig.ADDRESS + "/base/buploadin/queryCode";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				ParkImg = response.toString();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.showSuperToastAlertGreen(SubmitPark2.this, "404!!");
			}
		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

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
					Intent intent03 = new Intent(SubmitPark2.this, SubmitParkSucess.class);
					startActivity(intent03);
				} else {
					ToastUtils.showSuperToastAlert(SubmitPark2.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(SubmitPark2.this, "���ӷ�����ʧ��,���Ժ�����!");
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
					Intent intent03 = new Intent(SubmitPark2.this, SubmitParkSucess.class);
					startActivity(intent03);

				} else {
					ToastUtils.showSuperToastAlert(SubmitPark2.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(SubmitPark2.this, "���ӷ�����ʧ��,���Ժ�����!");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);

	}

	// ====================================================================================
	void resetStepNum() {
		ivStep01.setBackground(this.getResources().getDrawable(R.drawable.submit_1_n));
		ivStep02.setBackground(this.getResources().getDrawable(R.drawable.submit_2_n));
		ivStep03.setBackground(this.getResources().getDrawable(R.drawable.submit_3_n));

		tvStep01.setTextColor(this.getResources().getColor(R.color.submit_gray));
		tvStep02.setTextColor(this.getResources().getColor(R.color.submit_gray));
		tvStep03.setTextColor(this.getResources().getColor(R.color.submit_gray));

	}

	private void jumpStep() {
		resetStepNum();
		switch (flag) {
		case 1:
			tvStep01.setTextColor(this.getResources().getColor(R.color.submit_yellow));
			YoYo.with(Techniques.Shake).duration(1000).playOn(findViewById(R.id.ivStep01));
			ivStep01.setBackground(this.getResources().getDrawable(R.drawable.submit_1_p));
			break;
		case 2:
			tvStep02.setTextColor(this.getResources().getColor(R.color.submit_yellow));
			YoYo.with(Techniques.Shake).duration(1000).playOn(findViewById(R.id.ivStep02));
			ivStep02.setBackground(this.getResources().getDrawable(R.drawable.submit_2_p));
			ivLine02.setBackground(this.getResources().getDrawable(R.drawable.shenlv));
			break;
		case 3:
			tvStep03.setTextColor(this.getResources().getColor(R.color.submit_yellow));
			YoYo.with(Techniques.Shake).duration(1000).playOn(findViewById(R.id.ivStep03));
			ivStep03.setBackground(this.getResources().getDrawable(R.drawable.submit_3_p));
			ivLine03.setBackground(this.getResources().getDrawable(R.drawable.shenlv));
			break;

		default:
			break;
		}

	}

	public static void showBg() {
		linBg.setVisibility(View.VISIBLE);
	}

	public static void hideBg() {
		linBg.setVisibility(View.GONE);
	}
}
