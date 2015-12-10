package com.bepo.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bepo.utils.MyTextUtils;

public class TimelineView extends View {
	private Paint mPaint;
	String[] hour = { "0", "3", "6", "9", "12", "15", "18", "21", "24" };

	private int freeTimeStartFlag = -1;
	private int freeTimeSize = -1;

	ArrayList<HashMap<String, String>> timelineBookList = new ArrayList<HashMap<String, String>>();

	int bookTimeStartFlag;
	int bookTimeSize;

	String nowTime;
	int nowTimeFlag;

	Rect rect;

	public TimelineView(Context context, HashMap<String, String> map) {
		super(context);
		init(map);
	}

	public TimelineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		// this.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// postInvalidate();
		// Log.e("2=====", "2=====");
		// Log.e("freeTimeStartFlag", freeTimeStartFlag + "");
		// }
		//
		// });
	}

	public TimelineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init(HashMap<String, String> map) {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	void getNowTime() {
		long time = System.currentTimeMillis();
		final Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);
		nowTime = hour + ":" + minute;
	}

	// TODO
	@SuppressWarnings("unchecked")
	public void setSize(HashMap<String, Object> map) {

		Log.e("setSize=======", "setSize");

		// 计算当前时间
		getNowTime();
		Log.e("nowTime", nowTime);
		int n = Integer.parseInt(nowTime.split(":")[0]);// 小时
		int i = Integer.parseInt(nowTime.split(":")[1]);// 分钟
		this.nowTimeFlag = n * 12 + countTime(i);

		// 计算可租开始时间
		String fs = map.get("freeTimeStartFlag").toString();
		fs = MyTextUtils.noSpace(fs);
		int hs = Integer.parseInt(fs.split(":")[0]);
		int ms = Integer.parseInt(fs.split(":")[1]);
		this.freeTimeStartFlag = hs * 12 + countTime(ms);

		// 计算可租结束时间
		String fe = map.get("freeTimeEndFlag").toString();
		int he = Integer.parseInt(fe.split(":")[0]);
		int me = Integer.parseInt(fe.split(":")[1]);
		this.freeTimeSize = he * 12 + countTime(me);

		// 计算已租时间
		ArrayList<HashMap<String, String>> s = new ArrayList<HashMap<String, String>>();
		s = (ArrayList<HashMap<String, String>>) map.get("TIME_LIST");
		if (s != null) {
			for (HashMap<String, String> temp : s) {
				HashMap<String, String> hashmap = new HashMap<String, String>();
				hashmap.put("TYPE", temp.get("TYPE"));
				hashmap.put("BEGIN_TIME", temp.get("BEGIN_TIME"));
				hashmap.put("END_TIME", temp.get("END_TIME"));
				hashmap.put("DAY", temp.get("DAY"));
				timelineBookList.add(hashmap);
			}
		}

		postInvalidate();
	}

	private int countTime(int i) {
		if (i - 10 < 0) {
			if (i <= 5) {
				return 1;
			} else {
				return 2;
			}
		} else {
			int h = i / 10 * 2;
			int m = i % 10 <= 5 ? 1 : 2;
			return h + m;
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredWidth = 1024;
		int desiredHeight = 150;

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(desiredWidth, widthSize);
		} else {
			width = desiredWidth;
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(desiredHeight, heightSize);
		} else {
			height = desiredHeight;
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Log.e("ondraw=======", "ondraw");
		Log.e("freeTimeStartFlag", freeTimeStartFlag + "");

		// 24小时时间轴 第一根
		mPaint.setColor(Color.rgb(232, 237, 240));
		mPaint.setAlpha(255); // 设置透明度
		mPaint.setStyle(Style.FILL);
		canvas.drawRect(new Rect(0, 0, getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 50), mPaint);// 左边

		// Log.e("getMeasuredWidth=======", "" + getMeasuredWidth()); // 上边

		// 绘制文字
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize(23);
		mPaint.setColor(Color.rgb(116, 122, 124));
		for (int i = 0; i < hour.length; i++) {
			int spaceSize = getMeasuredWidth() / 24 * 3;
			String s = hour[i];
			if (i == 0) {
				canvas.drawText(s, 10, 85, mPaint);
			} else {
				canvas.drawText(s, spaceSize * i, 85, mPaint);
			}

		}

		// 24小时时间轴 第二根
		mPaint.setColor(Color.rgb(232, 237, 240));
		mPaint.setAlpha(255); // 设置透明度
		mPaint.setStyle(Style.FILL);
		canvas.drawRect(new Rect(0, 100, getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 150), mPaint);// 左边

		// 发布的时间
		int spaceSize = getMeasuredWidth() / (24 * 12);
		int sss = spaceSize * freeTimeStartFlag;
		int eee = spaceSize * freeTimeSize;
		Log.e("sss=======", "" + sss + "----" + eee);
		mPaint.setColor(Color.rgb(4, 177, 119));// 绿色
		mPaint.setAlpha(80);
		if (freeTimeStartFlag != -1 && freeTimeSize != -1) {
			canvas.drawRect(new Rect(spaceSize * freeTimeStartFlag, 0, spaceSize * freeTimeSize, 50), mPaint);
			canvas.drawRect(new Rect(spaceSize * freeTimeStartFlag, 100, spaceSize * freeTimeSize, 150), mPaint);

		}

		// 已租时间
		mPaint.setColor(Color.rgb(232, 237, 240));// 灰色
		mPaint.setAlpha(255);
		if (timelineBookList.size() > 0) {

			for (HashMap<String, String> temp : timelineBookList) {

				String ts = temp.get("BEGIN_TIME").toString();
				int he = Integer.parseInt(ts.split(":")[0]);
				int me = Integer.parseInt(ts.split(":")[1]);
				int s = he * 12 + countTime(me);

				String te = temp.get("END_TIME");
				int he1 = Integer.parseInt(te.split(":")[0]);
				int me1 = Integer.parseInt(te.split(":")[1]);
				int e = he1 * 12 + countTime(me1);

				if (temp.get("DAY").toString().equals("jin")) {

					if (temp.get("TYPE").toString().equals("shi")) {
						canvas.drawRect(new Rect(s * spaceSize, 0, e * spaceSize, 50), mPaint);
					} else {
						canvas.drawRect(new Rect(spaceSize * freeTimeStartFlag, 0, spaceSize * freeTimeSize, 50),
								mPaint);
					}

				} else {

					if (temp.get("TYPE").toString().equals("shi")) {
						canvas.drawRect(new Rect(s * spaceSize, 100, e * spaceSize, 150), mPaint);
					} else {
						canvas.drawRect(
								new Rect(spaceSize * freeTimeStartFlag, 100, spaceSize * freeTimeSize, 150),
								mPaint);
					}

				}

			}
		}
		// 直线轴
		// mPaint.setColor(Color.rgb(116, 122, 124)); // 设置画笔颜色
		// mPaint.setStrokeWidth((float) 1.0); // 设置线宽
		// // canvas.drawLine(8, 30, 8, 30, mPaint); // 绘制直线
		// canvas.drawLine(getMeasuredWidth() - getPaddingLeft(),
		// getPaddingTop(), getMeasuredWidth()
		// - getPaddingRight(), getMeasuredHeight() - getPaddingBottom(),
		// mPaint);

		// 当前时间
		mPaint.setColor(Color.rgb(227, 61, 56));// 橘色
		mPaint.setAlpha(220);
		canvas.drawRect(new Rect(nowTimeFlag * spaceSize, 0, nowTimeFlag * spaceSize + 5, 50), mPaint);
	}
}
