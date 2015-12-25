package com.yunt.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bepo.R;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.ui.AddPlate;

public class ChoseJianChengPop02 extends PopupWindow {

	private View view;
	GridView gridview;
	Context context;
	RelativeLayout rlCancel;

	ArrayList<HashMap<String, String>> lstImageItem = new ArrayList<HashMap<String, String>>();

	public ChoseJianChengPop02(Context context) {
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
			}
		});

		this.setContentView(view);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.FILL_PARENT);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		// ����SelectPicPopupWindow�������嶯��Ч��
		this.setAnimationStyle(R.style.AnimationFade);
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// // ����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		// mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = view.findViewById(R.id.lin4height).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

	private void initGridView() {

		try {
			InputStream is = context.getResources().openRawResource(R.raw.zimu);
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
				AddPlate.setZimu(lstImageItem.get(i).get("jc"));
				dismiss();

			}
		});

	}
}
