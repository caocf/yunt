package com.bepo.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.bepo.R;
import com.bepo.bean.EventParameter;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author simas
 * @describe 事件时间的弹出pop
 * @time 2015-5-14 16:17:07
 * 
 */

public class SelectEventTimePop extends PopupWindow {

	Context context;
	private View View;
	private CalendarPickerView calendar;
	public static String flag = "";

	@SuppressWarnings("deprecation")
	public SelectEventTimePop(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View = inflater.inflate(R.layout.sample_calendar_picker, null);
		initView();

		// 设置SelectPicPopupWindow的View
		this.setContentView(View);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.SelectEventFromStyle);
		// 实例化一个ColorDrawable颜色为半透明
		 ColorDrawable dw = new ColorDrawable(0xb0000000);
//		ColorDrawable dw = new ColorDrawable();
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		View.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = View.findViewById(R.id.calendar_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height || y > height) {
						dismiss();
					}
				}
				int width = View.findViewById(R.id.calendar_layout).getLeft();
				int x = (int) event.getX();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (x < width || x > width) {
						dismiss();
					}
				}

				return true;
			}
		});

	}

	private void initView() {
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -3);

		calendar = (CalendarPickerView) View.findViewById(R.id.calendar_view);
		calendar.init(lastYear.getTime(), nextYear.getTime()).inMode(SelectionMode.SINGLE).withSelectedDate(new Date());

		View.findViewById(R.id.done_button).setOnClickListener(new OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View view) {
				long toast = calendar.getSelectedDate().getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String sd = sdf.format(new Date(toast));
				EventParameter mEventParameter = new EventParameter();

				if (flag.equals("tvStartTime01")) {
					mEventParameter.setEventStartTime0(sd);
				} else if (flag.equals("tvEndTime01")) {
					mEventParameter.setEventEndTime0(sd);
				} else if (flag.equals("tvStartTime02")) {
					mEventParameter.setEventStartTime1(sd);
				} else if (flag.equals("tvEndTime02")) {
					mEventParameter.setEventEndTime1(sd);
				}

				EventBus.getDefault().post(mEventParameter);
				dismiss();
			}
		});
	}
}
