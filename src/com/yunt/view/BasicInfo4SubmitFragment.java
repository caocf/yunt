package com.yunt.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.ChekuTypeAdapter;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.bepo.view.MapLocation;
import com.yunt.ui.LocationTask;
import com.yunt.ui.OnLocationGetListener;
import com.yunt.ui.PositionEntity;

/**
 * @date 2015��11��04��10:45:16
 * @author kefanbufan
 * @���� �ύ��λ ������Ϣҳ
 */

public class BasicInfo4SubmitFragment extends Fragment implements OnClickListener, OnLocationGetListener {

	private LocationTask mLocationTask;
	View view;
	LinearLayout linMain;// ���ڵ��� pop �Ķ�λ,�ͱ������

	// ��ͼ��λ
	RelativeLayout rlLocation;// ��ͼ��λ�ⲿ����ؼ�
	static TextView tvMapAddress;// ��ͼ��λλ����ʾ
	String positionX, positionY;// ��ͼ��λ���صľ�γ������

	// С��ȫ��
	public static TextView etXiaoqu;// С��ȫ��,�ɸ��ݾ�γ�Ȼ�ȡ�б���ѡ��
	public static String CarParkCode;// ��ҵά���� С��code(���ڴ��� submipark2)

	// �������
	public static RelativeLayout rlChekuName;
	GridView TypeGridview;// ��������ѡ��ؼ�
	ChekuTypeAdapter chekuTypeAdapter;// ��������adapter
	public static String CodePosition = "1858";// �������� code(���ڴ��� submitpark2)

	EditText etDikuName;// ��������
	EditText etBianhao;// ��λ���

	// ���������
	RelativeLayout rlJiancheng;// ���Ƽ�Ʋ�����ߵ��ʱ�丽�ſؼ�
	static TextView tvJiancheng;// ���Ƽ�Ʋ���
	EditText etCarNumber;// �������ֲ���
	ChoseJianChengPop mChoseJianChengPop;// ʡ��Ƶ��� pop
	ArrayList<HashMap<String, String>> lstImageItem = new ArrayList<HashMap<String, String>>();// ʡ�������

	// static RelativeLayout rlMenjinName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.submit_basicinfo, container, false);

		etXiaoqu = (TextView) view.findViewById(R.id.etXiaoqu);
		etXiaoqu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), ChangXiaoquName.class);
				intent.putExtra("et", etXiaoqu.getText().toString());
				intent.putExtra("positionX", positionX);
				intent.putExtra("positionY", positionY);
				getActivity().startActivity(intent);
			}
		});

		etBianhao = (EditText) view.findViewById(R.id.etBianhao);
		etDikuName = (EditText) view.findViewById(R.id.etDikuName);

		etCarNumber = (EditText) view.findViewById(R.id.etCarNumber);
		etCarNumber.addTextChangedListener(tw);

		rlJiancheng = (RelativeLayout) view.findViewById(R.id.rlJiancheng);
		rlJiancheng.setOnClickListener(this);

		rlChekuName = (RelativeLayout) view.findViewById(R.id.rlChekuName);
		rlChekuName.setVisibility(View.GONE);

		rlLocation = (RelativeLayout) view.findViewById(R.id.rlLocation);
		rlLocation.setOnClickListener(this);

		linMain = (LinearLayout) view.findViewById(R.id.linMain);
		tvJiancheng = (TextView) view.findViewById(R.id.tvJiancheng);
		tvMapAddress = (TextView) view.findViewById(R.id.tvMapAddress);

		TypeGridview = (GridView) view.findViewById(R.id.TypeGridview);
		initData();

		// ��λ��ǰλ��
		mLocationTask = LocationTask.getInstance(getActivity());
		mLocationTask.setOnLocationGetListener(this);
		mLocationTask.startSingleLocate();

		return view;
	}

	private void initData() {

		String url = PathConfig.ADDRESS + "/sys/sysdicvalue/queryDicvalue?name=CODE_POSITION";
		url = MyTextUtils.urlPlusAndFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				ArrayList<HashMap<String, String>> typeData = new ArrayList<HashMap<String, String>>();
				String jsondata = response.toString();
				typeData.clear();
				typeData = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				for (int i = 0; i < typeData.size(); i++) {
					typeData.get(i).put("flag", "0");
				}

				chekuTypeAdapter = new ChekuTypeAdapter(typeData, getActivity());
				TypeGridview.setAdapter(chekuTypeAdapter);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	TextWatcher tw = new TextWatcher() {
		int index = 0;

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

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
	};

	public static void setJc(String text) {
		tvJiancheng.setText(text);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if

		(requestCode == PathConfig.LOCATION) {

			if (data != null) {
				String address = data.getExtras().getString("address");
				String MapPosition = data.getExtras().getString("position");

				positionX = MapPosition.split(",")[0].toString();
				positionY = MapPosition.split(",")[1].toString();
				tvMapAddress.setText(address);

				HashMap<String, String> xymap = new HashMap<String, String>();
				xymap.put("positionX", positionX);
				xymap.put("positionY", positionY);
				tvMapAddress.setTag(xymap);
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.rlJiancheng:

			mChoseJianChengPop = new ChoseJianChengPop(BasicInfo4SubmitFragment.this.getActivity());
			mChoseJianChengPop.showAtLocation(linMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.rlLocation:

			Intent intent = new Intent(getActivity(), MapLocation.class);
			intent.putExtra("x", positionX);
			intent.putExtra("y", positionY);
			startActivityForResult(intent, PathConfig.LOCATION);

			break;
		default:
			break;
		}

	}

	@Override
	public void onLocationGet(PositionEntity entity) {
		positionY = entity.latitue + "";
		positionX = entity.longitude + "";

		String temp = entity.address;
		temp = temp.replace(entity.city, "");
		tvMapAddress.setText(temp);

		HashMap<String, String> xymap = new HashMap<String, String>();
		xymap.put("positionX", positionX);
		xymap.put("positionY", positionY);
		tvMapAddress.setTag(xymap);

		// �黳����жϳ��Ƽ��
		try {
			InputStream is = getActivity().getResources().openRawResource(R.raw.jc);
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
	public void onDestroy() {
		super.onDestroy();
		mLocationTask.onDestroy();
	}

	@Override
	public void onRegecodeGet(PositionEntity entity) {

	}
}
