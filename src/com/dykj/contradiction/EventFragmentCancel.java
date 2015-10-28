package com.dykj.contradiction;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.bean.CommonBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.view.LodingPop;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class EventFragmentCancel extends Fragment {

	Context context;
	TextView tvTitle, tvContent, tvClose;
	ProgressBar progressBar;
	RelativeLayout rlBtn;
	String codes = "";
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.event_close_fragment, container, false);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvContent = (TextView) view.findViewById(R.id.tvContent);
		tvClose = (TextView) view.findViewById(R.id.tvClose);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		rlBtn = (RelativeLayout) view.findViewById(R.id.rlBtn);
		rlBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				closeEvent(codes);
				progressBar.setVisibility(View.VISIBLE);
				tvClose.setText("正在注销事件");
				YoYo.with(Techniques.BounceInUp).duration(500).playOn(view.findViewById(R.id.tvClose));
			}
		});

		HashMap<String, Object> eventInfo = new HashMap<String, Object>();
		eventInfo = ((EventDetail) getActivity()).getEventInfo();

		codes = eventInfo.get("CODE").toString();
		tvTitle.setText("事件标题: " + eventInfo.get("NAME_APPEAL").toString());

		if (eventInfo.get("APPEAL_CONTENT") != null) {
			tvContent.setText("事件描述: " + eventInfo.get("APPEAL_CONTENT").toString());
			tvContent.setVisibility(View.GONE);
		}

		return view;
	}

	protected void closeEvent(String code) {
		String url = PathConfig.ADDRESS + "gsm/event/eevent/deleteEvent?codes=" + code + "&ukey=" + PathConfig.ukey;

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@SuppressWarnings({ "rawtypes" })
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				Map<String, Object> map = JSON.parseObject(jsondata);
				if ((Boolean) map.get("status")) {
					progressBar.setVisibility(View.GONE);
					ToastUtils.showSuperToastAlertGreen(getActivity(), "注销成功");
					tvClose.setText("注销事件");
					YoYo.with(Techniques.BounceInUp).duration(500).playOn(view.findViewById(R.id.tvClose));
					rlBtn.setClickable(false);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							getActivity().finish();
						}
					}, 500);

				} else {
					ToastUtils.showSuperToastAlert(getActivity(), "注销失败,请重试！");
					progressBar.setVisibility(View.GONE);
					tvClose.setText("注销事件");
					YoYo.with(Techniques.BounceInUp).duration(500).playOn(view.findViewById(R.id.tvClose));
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// ToastUtils.showSuperToastAlert(EventQueryAll.this,
				// "连接服务器失败，请确认网络畅通后重试！");
			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

}