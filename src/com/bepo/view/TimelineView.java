package com.bepo.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TimelineView extends View {
	private Paint mPaint;
	String[] hour = { "0", "3", "6", "9", "12", "15", "18", "21", "24" };

	// 不跨天的开始时间和长度
	private int freeTimeStartFlag = -1;
	private int freeTimeSize = -1;

	// 跨天的开始时间和长度

	private int kuatianS = 0;// 开始时间为当日 0时
	private int kuatianE = -1;// 结束时间为传递过来的结束时间

	private int kuatianS2 = -1;// 开始时间为传递过来的开始时间
	private int kuatianE2 = 24;// 结束时间为当日 24时

	ArrayList<HashMap<String, String>> timelineBookList = new ArrayList<HashMap<String, String>>();

	int bookTimeStartFlag;
	int bookTimeSize;

	String nowTime;
	int nowTimeFlag;
	Boolean isKuaTian = true;
	Rect rect;

	public TimelineView(Context context, HashMap<String, String> map) {
		super(context);
		init(map);
	}

	public TimelineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				postInvalidate();
				ToastUtils.showSuperToastAlertGreen(getContext(), "蓝色区域为可租,灰色区域为不可租");
			}

		});
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

		// Log.e("setSize=======", "setSize");

		// 计算当前时间
		getNowTime();
		// Log.e("nowTime", nowTime);
		int n = Integer.parseInt(nowTime.split(":")[0]);// 小时
		int i = Integer.parseInt(nowTime.split(":")[1]);// 分钟
		this.nowTimeFlag = n * 12 + countTime(i);

		// 判断是否跨天
		String fs = map.get("freeTimeStartFlag").toString();
		String fe = map.get("freeTimeEndFlag").toString();

		// 计算可租开始时间
		// fs = MyTextUtils.noSpace(fs);
		int hs = Integer.parseInt(fs.split(":")[0]);
		int ms = Integer.parseInt(fs.split(":")[1]);

		// 计算可租结束时间
		int he = Integer.parseInt(fe.split(":")[0]);
		int me = Integer.parseInt(fe.split(":")[1]);

		// 如果结束时间的时针(取整)小于开始时间的分针(取整),那么就是跨天
		if (he - hs < 0) {
			isKuaTian = true;

			// 第一段时间
			kuatianE = he * 12 + countTime(me);
			// 第二段时间
			kuatianS2 = hs * 12 + countTime(ms);
			// Log.e("ddd", kuatianS2 + "");

		} else {
			isKuaTian = false;
			this.freeTimeStartFlag = hs * 12 + countTime(ms);
			this.freeTimeSize = he * 12 + countTime(me);
			Log.e("freetimesize====", freeTimeSize + "");
		}

		// 计算已租时间
		ArrayList<HashMap<String, String>> s = new ArrayList<HashMap<String, String>>();
		if (!MyTextUtils.isEmpty(map.get("bookList").toString())) {
			s = (ArrayList<HashMap<String, String>>) JSON.parseObject(map.get("bookList").toString(),
					new TypeReference<ArrayList<HashMap<String, String>>>() {
					});
		}
		timelineBookList.clear();
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

	// 计算[分针]所占的时间轴比例
	// 算法说明
	//
	private int countTime(int i) {
		if (i - 10 < 0) {

			// 初始值为0的返回0
			if (i == 0) {
				return 0;
			}
			// 初始分针不足五分钟的 按五分钟计算,所占面积提现为1格
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

		// 24小时时间轴 第一根
		mPaint.setColor(Color.rgb(232, 237, 240));
		mPaint.setAlpha(255); // 设置透明度
		mPaint.setStyle(Style.FILL);
		canvas.drawRect(new Rect(0, 0, getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 50), mPaint);// 左边

		// 绘制文字
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize(26);
		mPaint.setColor(Color.rgb(116, 122, 124));
		for (int i = 0; i < hour.length; i++) {
			int spaceSize = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 24 * 3;
			String s = hour[i];
			if (i == 0) {
				canvas.drawText(s, 10, 85, mPaint);
			} else if (i == 8) {
				canvas.drawText(s, spaceSize * i - 8, 85, mPaint);
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
		int tempint = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		float spaceSize = (float) tempint / (24 * 12);
		// mPaint.setColor(Color.rgb(4, 177, 119));// 绿色
		mPaint.setColor(Color.rgb(37, 124, 254));// 蓝色
		mPaint.setAlpha(100);

		if (isKuaTian) {
			// 今天轴
			canvas.drawRect(new RectF(spaceSize * kuatianS, 0, spaceSize * kuatianE, 50), mPaint);
			canvas.drawRect(new RectF(spaceSize * kuatianS2, 0, getMeasuredWidth() - getPaddingLeft()
					- getPaddingRight(), 50), mPaint);
			// 明天轴
			canvas.drawRect(new RectF(spaceSize * kuatianS, 100, spaceSize * kuatianE, 150), mPaint);
			canvas.drawRect(new RectF(spaceSize * kuatianS2, 100, getMeasuredWidth() - getPaddingLeft()
					- getPaddingRight(), 150), mPaint);
		} else {
			if (freeTimeStartFlag != -1 && freeTimeSize != -1) {
				canvas.drawRect(new RectF(spaceSize * freeTimeStartFlag, 0, spaceSize * freeTimeSize, 50), mPaint);
				canvas.drawRect(new RectF(spaceSize * freeTimeStartFlag, 100, spaceSize * freeTimeSize, 150),
						mPaint);
			}
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
						canvas.drawRect(new RectF(s * spaceSize, 0, e * spaceSize, 50), mPaint);
					} else {
						canvas.drawRect(new RectF(spaceSize * freeTimeStartFlag, 0, spaceSize * freeTimeSize, 50),
								mPaint);
					}

				} else {

					if (temp.get("TYPE").toString().equals("shi")) {
						canvas.drawRect(new RectF(s * spaceSize, 100, e * spaceSize, 150), mPaint);
					} else {
						canvas.drawRect(new RectF(spaceSize * freeTimeStartFlag, 100, spaceSize * freeTimeSize,
								150), mPaint);
					}

				}

			}
		}

		// 当前时间
		mPaint.setColor(Color.rgb(227, 61, 56));// 橘色
		mPaint.setAlpha(220);
		canvas.drawRect(new RectF(nowTimeFlag * spaceSize, 0, nowTimeFlag * spaceSize + 5, 50), mPaint);
	}
}
