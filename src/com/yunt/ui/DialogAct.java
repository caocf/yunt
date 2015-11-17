package com.yunt.ui;

import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class DialogAct extends BaseAct {

	String code; // ³µÎ» code
	TextView tvCancel, tvYes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		code = getIntent().getExtras().getString("code");
		tvYes = (TextView) this.findViewById(R.id.tvYes);
		tvYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				deletData();
			}
		});
		tvCancel = (TextView) this.findViewById(R.id.tvCancel);
		tvCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void deletData() {
		showDialog();
		String url = PathConfig.ADDRESS + "/base/breleasepark/delete?codes=" + code + ",";
		url = MyTextUtils.urlPlusAndFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				showDialog();
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				dismissDialog();

				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(DialogAct.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(DialogAct.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}
}
