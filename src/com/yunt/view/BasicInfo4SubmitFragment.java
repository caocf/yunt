package com.yunt.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bepo.R;
import com.bepo.bean.AllGridTreeBean;
import com.bepo.core.PathConfig;
import com.bepo.utils.CameraUtil;
import com.bepo.view.MapLocation;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.ui.LocationTask;
import com.yunt.ui.OnLocationGetListener;
import com.yunt.ui.PositionEntity;
import com.yunt.ui.SubmitPark;

public class BasicInfo4SubmitFragment extends Fragment implements OnClickListener, OnLocationGetListener {

	public View view;
	EditText etXiaoqu, etBianhao, etCarNumber;
	RelativeLayout rlJiancheng, rlLocation;
	static TextView tvJiancheng, tvMapAddress;
	ChoseJianChengPop mChoseJianChengPop;
	LinearLayout linMain;

	String positionX, positionY;
	private LocationTask mLocationTask;
	ArrayList<HashMap<String, String>> lstImageItem = new ArrayList<HashMap<String, String>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.submit_basicinfo, container, false);

		etXiaoqu = (EditText) view.findViewById(R.id.etXiaoqu);
		etBianhao = (EditText) view.findViewById(R.id.etBianhao);

		etCarNumber = (EditText) view.findViewById(R.id.etCarNumber);
		etCarNumber.addTextChangedListener(tw);

		rlJiancheng = (RelativeLayout) view.findViewById(R.id.rlJiancheng);
		rlJiancheng.setOnClickListener(this);

		rlLocation = (RelativeLayout) view.findViewById(R.id.rlLocation);
		rlLocation.setOnClickListener(this);

		linMain = (LinearLayout) view.findViewById(R.id.linMain);
		tvJiancheng = (TextView) view.findViewById(R.id.tvJiancheng);
		tvMapAddress = (TextView) view.findViewById(R.id.tvMapAddress);

		// 定位当前位置
		mLocationTask = LocationTask.getInstance(getActivity());
		mLocationTask.setOnLocationGetListener(this);
		mLocationTask.startSingleLocate();

		return view;
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
			etCarNumber.removeTextChangedListener(this);// 解除文字改变事件
			index = etCarNumber.getSelectionStart();// 获取光标位置
			etCarNumber.setText(s.toString().toUpperCase());
			etCarNumber.setSelection(index);// 重新设置光标位置
			etCarNumber.addTextChangedListener(this);// 重新绑定事件
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

		// 情怀般的判断车牌简称
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
