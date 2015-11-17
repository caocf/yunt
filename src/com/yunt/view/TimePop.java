package com.yunt.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.bepo.R;
import com.bepo.view.Time4PopBean;
import com.yunt.ui.SubmitPark2;

import de.greenrobot.event.EventBus;

public class TimePop extends PopupWindow {

	private View view;
	private TimePicker timePick;
	Context context;
	RelativeLayout rlCancel;
	TextView tvCancle, tvYes;
	Time4PopBean timeBean;
	String flag;

	ArrayList<HashMap<String, String>> lstImageItem = new ArrayList<HashMap<String, String>>();

	public TimePop(Context context, String flag) {
		super(context);
		this.context = context;
		this.flag = flag;
		timeBean = new Time4PopBean();

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.time_pop, null);

		timePick = (TimePicker) view.findViewById(R.id.timepicker);
		timePick.setIs24HourView(true);
		timeBean.setTime(timePick.getCurrentHour() + ":" + timePick.getCurrentMinute());
		timeBean.setFlag(flag);
		Log.e("time", timePick.getCurrentHour() + ":" + timePick.getCurrentMinute());

		timePick.setOnTimeChangedListener(new TimeListener());

		tvYes = (TextView) view.findViewById(R.id.tvYes);
		tvYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				EventBus.getDefault().post(timeBean);
				dismiss();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						SubmitPark2.hideBg();
					}

				}, 300);
			}
		});

		tvCancle = (TextView) view.findViewById(R.id.tvCancel);
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						SubmitPark2.hideBg();
					}

				}, 300);
			}
		});

		this.setContentView(view);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationFade);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// // 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = view.findViewById(R.id.lin4height).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
						new Handler().postDelayed(new Runnable() {
							public void run() {
								SubmitPark2.hideBg();
							}

						}, 300);

					}
				}
				return true;
			}
		});

		new Handler().postDelayed(new Runnable() {
			public void run() {
				SubmitPark2.showBg();
			}

		}, 300);

	}

	class TimeListener implements OnTimeChangedListener {
		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			timeBean.setTime(hourOfDay + ":" + minute);

		}
	}
}
