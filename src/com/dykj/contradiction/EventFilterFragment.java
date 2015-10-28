package com.dykj.contradiction;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.bean.EventParameter;
import com.bepo.bean.EventSearchUrlBean;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.bepo.view.SelectEventAppraisePop;
import com.bepo.view.SelectEventFromPop;
import com.bepo.view.SelectEventStatusPop;
import com.bepo.view.SelectEventTimePop;
import com.bepo.view.SelectEventTypePop;

import de.greenrobot.event.EventBus;

public class EventFilterFragment extends Fragment implements OnClickListener {
	LinearLayout linALL;

	EditText etName, etSuqiuren;
	private static SelectEventFromPop mSelectEventFromPop;// 事件来源pop
	private static SelectEventTypePop mSelectEventTypePop;// 事件类型pop
	private static SelectEventStatusPop mSelectEventStatusPop;// 事件状态pop
	private static SelectEventAppraisePop mSelectEventAppraisePop;// 满意度pop
	private static SelectEventTimePop mSelectEventTimePop;// 事件时间pop
	TextView tvEventFrom, tvEventStatus, tvEventYes, tvEventType;
	RelativeLayout rlEventFrom, rlEventType, rlEventStatus, rlEventYes;
	TextView tvStartTime01, tvEndTime01, tvStartTime02, tvEndTime02;
	Button btn_search;

	String eventName = "";
	String eventSuqiuren = "";
	String eventTypeCode = "";
	String eventFromCode = "";
	String eventStatusCode = "";
	String eventYesCode = "";
	String startTime1 = "";
	String endTime1 = "";
	String startTime2 = "";
	String endTime2 = "";
	String time1 = "";
	String time2 = "";
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.event_filter_pop1, container, false);
		EventBus.getDefault().register(this);
		initView();
		return view;
	}

	private void initView() {

		linALL = (LinearLayout) view.findViewById(R.id.linALL);

		etName = (EditText) view.findViewById(R.id.etName);
		etSuqiuren = (EditText) view.findViewById(R.id.etSuqiuren);

		rlEventFrom = (RelativeLayout) view.findViewById(R.id.rlEventFrom);
		rlEventType = (RelativeLayout) view.findViewById(R.id.rlEventType);
		rlEventStatus = (RelativeLayout) view.findViewById(R.id.rlEventStatus);
		rlEventYes = (RelativeLayout) view.findViewById(R.id.rlEventYes);

		rlEventFrom.setOnClickListener(this);
		rlEventType.setOnClickListener(this);
		rlEventStatus.setOnClickListener(this);
		rlEventYes.setOnClickListener(this);

		tvEventFrom = (TextView) view.findViewById(R.id.tvEventFrom);
		tvEventStatus = (TextView) view.findViewById(R.id.tvEventStatus);
		tvEventYes = (TextView) view.findViewById(R.id.tvEventYes);
		tvEventType = (TextView) view.findViewById(R.id.tvEventType);

		tvStartTime01 = (TextView) view.findViewById(R.id.tvStartTime01);
		tvEndTime01 = (TextView) view.findViewById(R.id.tvEndTime01);
		tvStartTime02 = (TextView) view.findViewById(R.id.tvStartTime02);
		tvEndTime02 = (TextView) view.findViewById(R.id.tvEndTime02);

		tvStartTime01.setOnClickListener(this);
		tvEndTime01.setOnClickListener(this);
		tvStartTime02.setOnClickListener(this);
		tvEndTime02.setOnClickListener(this);

		btn_search = (Button) view.findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(EventParameter mEventParameter) {

		if (!MyTextUtils.isEmpty(mEventParameter.getEventStartTime0())) {
			tvStartTime01.setText(mEventParameter.getEventStartTime0());
			startTime1 = mEventParameter.getEventStartTime0();
		}
		if (!MyTextUtils.isEmpty(mEventParameter.getEventEndTime0())) {
			tvEndTime01.setText(mEventParameter.getEventEndTime0());
			endTime1 = mEventParameter.getEventEndTime0();
		}
		if (!MyTextUtils.isEmpty(mEventParameter.getEventStartTime1())) {
			tvStartTime02.setText(mEventParameter.getEventStartTime1());
			startTime2 = mEventParameter.getEventStartTime1();
		}
		if (!MyTextUtils.isEmpty(mEventParameter.getEventEndTime1())) {
			tvEndTime02.setText(mEventParameter.getEventEndTime1());
			endTime2 = mEventParameter.getEventEndTime1();
		}
		// 事件类型
		if (!MyTextUtils.isEmpty(mEventParameter.getEventType().getNAME_C())) {
			tvEventType.setText(mEventParameter.getEventType().getNAME_C());
			eventTypeCode = mEventParameter.getEventType().getCODE();
		}
		// 满意度
		if (mEventParameter.getEventYesMap().size() > 0) {
			tvEventYes.setText(mEventParameter.getEventYesMap().get("NAME_C"));
			eventYesCode = mEventParameter.getEventYesMap().get("CODE");
		}
		// 事件来源
		if (mEventParameter.getEventFromMap().size() > 0) {
			tvEventFrom.setText(mEventParameter.getEventFromMap().get("NAME_C"));
			eventFromCode = mEventParameter.getEventFromMap().get("CODE");
		}

		// 事件状态
		if (!MyTextUtils.isEmpty(mEventParameter.getEventStatus())) {
			tvEventStatus.setText(mEventParameter.getEventStatus().split(":")[1]);
			eventStatusCode = mEventParameter.getEventStatus().split(":")[0];
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rlEventFrom:

			mSelectEventFromPop = new SelectEventFromPop(getActivity(), null);
			mSelectEventFromPop.showAsDropDown(rlEventFrom);

			break;
		case R.id.rlEventStatus:

			mSelectEventStatusPop = new SelectEventStatusPop(getActivity(), null);
			mSelectEventStatusPop.showAtLocation(linALL, Gravity.CENTER, 0, 0);
			break;

		case R.id.rlEventYes:

			mSelectEventAppraisePop = new SelectEventAppraisePop(getActivity(), null);
			mSelectEventAppraisePop.showAsDropDown(rlEventYes);
			break;

		case R.id.rlEventType:

			mSelectEventTypePop = new SelectEventTypePop(getActivity(), null);
			mSelectEventTypePop.showAtLocation(linALL, Gravity.CENTER, 0, 0);
			break;

		case R.id.tvStartTime01:
			SelectEventTimePop.flag = "tvStartTime01";
			mSelectEventTimePop = new SelectEventTimePop(getActivity(), null);
			mSelectEventTimePop.showAtLocation(linALL, Gravity.CENTER, 0, 0);
			// ToastUtils.showSuperToastComment(this, "上下滑动，选择开始日期");
			break;
		case R.id.tvEndTime01:
			SelectEventTimePop.flag = "tvEndTime01";
			mSelectEventTimePop = new SelectEventTimePop(getActivity(), null);
			mSelectEventTimePop.showAtLocation(linALL, Gravity.CENTER, 0, 0);
			// ToastUtils.showSuperToastComment(this, "上下滑动，选择开始日期");
			break;
		case R.id.tvStartTime02:
			SelectEventTimePop.flag = "tvStartTime02";
			mSelectEventTimePop = new SelectEventTimePop(getActivity(), null);
			mSelectEventTimePop.showAtLocation(linALL, Gravity.CENTER, 0, 0);
			// ToastUtils.showSuperToastComment(this, "上下滑动，选择开始日期");
			break;
		case R.id.tvEndTime02:
			SelectEventTimePop.flag = "tvEndTime02";
			mSelectEventTimePop = new SelectEventTimePop(getActivity(), null);
			mSelectEventTimePop.showAtLocation(linALL, Gravity.CENTER, 0, 0);
			// ToastUtils.showSuperToastComment(this, "上下滑动，选择开始日期");
			break;

		case R.id.btn_search:

			if (!startTime1.equals("") || !endTime1.equals("")) {
				time1 = ",START_DATEbetween" + startTime1 + ":" + endTime1;
				// time1 = startTime1 + ":" + endTime1;
			}

			// } else {
			//
			// // if (startTime1.equals("") | endTime1.equals("")) {
			// //
			// // ToastUtils.showSuperToastAlert(this, "开始时间或结束时间不能为空");
			// // return;
			// // } else {
			// // if (startTime1.compareTo(endTime1) > 0) {
			// // ToastUtils.showSuperToastAlert(this, " 结束时间 小于 开始时间");
			// // return;
			// // }
			//
			// // }
			//
			// }
			if (!startTime2.equals("") || !endTime2.equals("")) {
				// time2 = startTime2 + ":" + endTime2;
				time2 = ",CLOSETIMEbetween" + startTime2 + ":" + endTime2;
			}

			eventName = "NAME_APPEALlike" + etName.getText().toString().trim();

			eventSuqiuren = MyTextUtils.isEmpty(etSuqiuren.getText().toString().trim()) ? "" : "APPEAL_NAME="
					+ etSuqiuren.getText().toString().trim();

			eventTypeCode = MyTextUtils.isEmpty(eventTypeCode) ? "" : ",CODE_EVENT_TYPE=" + eventTypeCode;

			eventFromCode = MyTextUtils.isEmpty(eventFromCode) ? "" : ",CODE_EVENT_FROM=" + eventFromCode;

			eventStatusCode = MyTextUtils.isEmpty(eventStatusCode) ? "" : ",STEP_ID=" + eventStatusCode;

			eventYesCode = MyTextUtils.isEmpty(eventYesCode) ? "" : ",CODE_APPRAISE=" + eventYesCode;

			String url = eventName + eventSuqiuren + eventTypeCode + eventFromCode + eventStatusCode + eventYesCode
					+ time1 + time2;
			// Log.e("条件筛查URL==========================", url);

			EventSearchUrlBean esub = new EventSearchUrlBean();
			// esub.setEventName(eventName);
			// esub.setEventSuqiuren(eventSuqiuren);
			// esub.setEventStatusCode(eventStatusCode);
			// esub.setEventTypeCode(eventTypeCode);
			// esub.setEventYesCode(eventYesCode);
			// esub.setEventFromCode(eventFromCode);
			// esub.setTime1(endTime1);
			// esub.setTime2(endTime2);
			esub.setUrl(url);

			EventBus.getDefault().post(esub);
			getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

			break;
		default:
			break;
		}

	}
}
