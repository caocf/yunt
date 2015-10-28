package com.bepo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bepo.R;

/**
 * 
 * @author simas
 * @describe 通用的等待响应pop
 * @time 2015-5-18 16:37:13
 * 
 */

public class LodingPop extends PopupWindow {

	Context context;
	private View View;
	ImageView loading;

	@SuppressWarnings("deprecation")
	public LodingPop(Activity context, OnClickListener itemsOnClick) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View = inflater.inflate(R.layout.loading, null);

//		loading = (ImageView) View.findViewById(R.id.ivLoading);// 放置的ImageView控件
		// 设置动画背景
//		loading.setBackgroundResource(R.anim.loading);// 其中R.anim.animation_list就是上一步准备的动画描述文件的资源名
		// 获得动画对象

		AnimationDrawable _animaition = (AnimationDrawable) loading.getBackground();

		_animaition.setOneShot(false);
		if (_animaition.isRunning())// 是否正在运行？

		{
			_animaition.stop();// 停止

		}
		_animaition.start();// 启动

		// 设置SelectPicPopupWindow的View
		this.setContentView(View);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		// this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		// this.setAnimationStyle(R.style.SelectEventFromStyle);
		this.setAnimationStyle(R.style.commondialog);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//		View.setOnTouchListener(new OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				int height = View.findViewById(R.id.event_type_layout).getTop();
//				int y = (int) event.getY();
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					if (y < height || y > height) {
//						dismiss();
//					}
//				}
//				int width = View.findViewById(R.id.event_type_layout).getLeft();
//				int x = (int) event.getX();
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					if (x < width || x > width) {
//						dismiss();
//					}
//				}
//
//				return true;
//			}
//		});

	}
}
