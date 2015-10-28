package com.yunt.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bepo.R;
import com.bepo.ui.LoginAct;
import com.bepo.ui.MainAct;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.ui.SubmitPark2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ChoseJianChengPop extends PopupWindow {

	private View view;
	GridView gridview;
	Context context;
	RelativeLayout rlCancel;

	ArrayList<HashMap<String, String>> lstImageItem = new ArrayList<HashMap<String, String>>();

	public ChoseJianChengPop(Context context) {
		super(context);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.chose_jc_pop, null);

		gridview = (GridView) view.findViewById(R.id.gridview);
		initGridView();

		rlCancel = (RelativeLayout) view.findViewById(R.id.rlCancel);
		rlCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
				SubmitPark2.hideBg();
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

	private void initGridView() {

		try {
			InputStream is = context.getResources().openRawResource(R.raw.jc);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			String json = new String(buffer);
			lstImageItem = (ArrayList<HashMap<String, String>>) JSON.parseObject(json,
					new TypeReference<ArrayList<HashMap<String, String>>>() {
					});
		} catch (Exception e) {
		}

		SimpleAdapter mSimpleAdapter = new SimpleAdapter(context, lstImageItem, R.layout.jc_item,
				new String[] { "jc" }, new int[] { R.id.tvJc });

		gridview.setAdapter(mSimpleAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int i, long arg3) {
				// ToastUtils.showSuperToastAlert(context,
				// lstImageItem.get(i).get("jc"));
				BasicInfo4SubmitFragment.setJc(lstImageItem.get(i).get("jc"));
				dismiss();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						SubmitPark2.hideBg();
					}

				}, 100);

			}
		});

	}
}
