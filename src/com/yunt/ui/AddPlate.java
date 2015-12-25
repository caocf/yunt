package com.yunt.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.view.ChoseJianChengPop01;
import com.yunt.view.ChoseJianChengPop02;

public class AddPlate extends BaseAct implements OnLocationGetListener {

	LocationTask mLocationTask;
	public static TextView tvJiancheng;
	public static TextView tvZimu;
	public static TextView tvSubmit;
	EditText etCarNumber;

	LinearLayout linMain;
	LinearLayout linBack;
	RelativeLayout rlJiancheng;
	RelativeLayout rlZimu;
	ChoseJianChengPop01 mChoseJianChengPop;
	ChoseJianChengPop02 mChoseJianChengPop02;
	ArrayList<HashMap<String, String>> lstImageItem = new ArrayList<HashMap<String, String>>();// ʡ�������

	String Plate1 = "";
	String Plate2 = "";
	String Plate3 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
		setContentView(R.layout.add_plate);

		// ��λ��ǰλ��
		mLocationTask = LocationTask.getInstance(getApplicationContext());
		mLocationTask.setOnLocationGetListener(this);
		mLocationTask.startSingleLocate();

		initView();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

	public static void setJiancheng(String s) {
		tvJiancheng.setText(s);
	}

	public static void setZimu(String s) {
		tvZimu.setText(s);
	};

	private void initView() {
		getTopBar("��ӳ���");

		linMain = (LinearLayout) this.findViewById(R.id.linMain);

		rlJiancheng = (RelativeLayout) this.findViewById(R.id.rlJiancheng);
		rlJiancheng.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mChoseJianChengPop = new ChoseJianChengPop01(AddPlate.this);
				mChoseJianChengPop.showAtLocation(linMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

			}
		});

		linBack = (LinearLayout) this.findViewById(R.id.linBack);
		linBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		rlZimu = (RelativeLayout) this.findViewById(R.id.rlZimu);
		rlZimu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mChoseJianChengPop02 = new ChoseJianChengPop02(AddPlate.this);
				mChoseJianChengPop02.showAtLocation(linMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

			}
		});

		tvJiancheng = (TextView) this.findViewById(R.id.tvJiancheng);
		tvZimu = (TextView) this.findViewById(R.id.tvZimu);
		tvSubmit = (TextView) this.findViewById(R.id.tvSubmit);
		tvSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Plate1 = tvJiancheng.getText().toString().trim();
				Plate2 = tvZimu.getText().toString().trim();
				Plate3 = etCarNumber.getText().toString().trim();
				if (MyTextUtils.isEmpty(Plate1)) {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "��Ʋ���Ϊ��");
				} else if (MyTextUtils.isEmpty(Plate2)) {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "�д��벻��Ϊ��");
				} else if (MyTextUtils.isEmpty(Plate3)) {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "���ƺŲ���Ϊ��");
				} else if (Plate3.length() < 5) {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "���ƺ�λ������");
				} else {
					submitPlate();
				}

			}
		});

		etCarNumber = (EditText) this.findViewById(R.id.etCarNumber);
		etCarNumber.addTextChangedListener(new TextWatcher() {
			int index = 0;

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				etCarNumber.removeTextChangedListener(this);// ������ָı��¼�
				index = etCarNumber.getSelectionStart();// ��ȡ���λ��
				etCarNumber.setText(s.toString().toUpperCase());
				etCarNumber.setSelection(index);// �������ù��λ��
				etCarNumber.addTextChangedListener(this);// ���°��¼�
			}
		});

	}

	protected void submitPlate() {
		showDialog();
		String url;

		url = PathConfig.ADDRESS + "/base/buserplate/add";
		url = MyTextUtils.urlPlusFoot(url);

		Map<String, String> params = new HashMap<String, String>();
		params.put("Plate1", Plate1);
		params.put("Plate2", Plate2);
		params.put("Plate3", Plate3);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(AddPlate.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(AddPlate.this, message.get("info"));
					finish();
				}
				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(AddPlate.this, "���ӷ�����ʧ��,���Ժ�����!");
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);
	}

	@Override
	public void onLocationGet(PositionEntity entity) {
		// �黳����жϳ��Ƽ��
		try {
			InputStream is = getApplicationContext().getResources().openRawResource(R.raw.jc);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			String json = new String(buffer);
			lstImageItem = (ArrayList<HashMap<String, String>>) JSON.parseObject(json,
					new TypeReference<ArrayList<HashMap<String, String>>>() {
					});
		} catch (Exception e) {
		}

		for (int i = 0; i < lstImageItem.size(); i++) {
			if (lstImageItem.get(i).get("province").equals(entity.province)) {
				tvJiancheng.setText(lstImageItem.get(i).get("jc"));
			}

		}
	}

	@Override
	public void onRegecodeGet(PositionEntity entity) {

	}

}
