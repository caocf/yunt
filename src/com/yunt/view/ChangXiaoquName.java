package com.yunt.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;

public class ChangXiaoquName extends BaseAct implements OnClickListener {

	LinearLayout linBack;
	RelativeLayout rlRight;

	AutoCompleteTextView keyWord;
	ImageView ivClose;

	ListView xiaoquListview;
	ArrayList<HashMap<String, String>> CommunityList = new ArrayList<HashMap<String, String>>();
	public static SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
		setContentView(R.layout.chang_xiaoqu_name);
		initView();
	}

	private void initView() {
		if (!MyTextUtils.isEmpty(getIntent().getExtras().getString("positionX").toString())) {
			getCommunityList(getIntent().getExtras().getString("positionX"),
					getIntent().getExtras().getString("positionY"));

		}

		linBack = (LinearLayout) this.findViewById(R.id.linBack);
		linBack.setOnClickListener(this);

		rlRight = (RelativeLayout) this.findViewById(R.id.rlRight);
		rlRight.setOnClickListener(this);

		keyWord = (AutoCompleteTextView) this.findViewById(R.id.keyWord);
		keyWord.setText(getIntent().getExtras().getString("et"));
		keyWord.addTextChangedListener(tw01);

		ivClose = (ImageView) this.findViewById(R.id.ivClose);
		ivClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				keyWord.setText("");
			}
		});

		xiaoquListview = (ListView) this.findViewById(R.id.xiaoquListview);

	}

	private void getCommunityList(String lon, String lat) {
		String url = PathConfig.ADDRESS + "/base/bcarpark/queryByPosition?lon=" + lon + "&lat=" + lat;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				CommunityList = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				adapter = new SimpleAdapter(getApplicationContext(), CommunityList, R.layout.list_items,
						new String[] { "CAR_PARK_NAME" }, new int[] { R.id.tvxiaoqu });
				xiaoquListview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				xiaoquListview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						BasicInfo4SubmitFragment.etXiaoqu.setText(CommunityList.get(arg2).get("CAR_PARK_NAME"));
						BasicInfo4SubmitFragment.CarParkCode = CommunityList.get(arg2).get("CODE").toString();
						finish();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linBack:
			finish();
			break;

		case R.id.rlRight:

			BasicInfo4SubmitFragment.etXiaoqu.setText(keyWord.getText().toString().trim());
			BasicInfo4SubmitFragment.CarParkCode = "0";
			finish();
			break;

		default:
			break;
		}
	}

	TextWatcher tw01 = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (MyTextUtils.isEmpty(s.toString())) {
				ivClose.setVisibility(View.GONE);
			} else {
				ivClose.setVisibility(View.VISIBLE);
			}

		}
	};

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

}
