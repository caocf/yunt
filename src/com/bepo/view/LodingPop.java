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
 * @describe ͨ�õĵȴ���Ӧpop
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

//		loading = (ImageView) View.findViewById(R.id.ivLoading);// ���õ�ImageView�ؼ�
		// ���ö�������
//		loading.setBackgroundResource(R.anim.loading);// ����R.anim.animation_list������һ��׼���Ķ��������ļ�����Դ��
		// ��ö�������

		AnimationDrawable _animaition = (AnimationDrawable) loading.getBackground();

		_animaition.setOneShot(false);
		if (_animaition.isRunning())// �Ƿ��������У�

		{
			_animaition.stop();// ֹͣ

		}
		_animaition.start();// ����

		// ����SelectPicPopupWindow��View
		this.setContentView(View);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.FILL_PARENT);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		// this.setFocusable(true);
		// ����SelectPicPopupWindow�������嶯��Ч��
		// this.setAnimationStyle(R.style.SelectEventFromStyle);
		this.setAnimationStyle(R.style.commondialog);
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// ����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		// mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
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
