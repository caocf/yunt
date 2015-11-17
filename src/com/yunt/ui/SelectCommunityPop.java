package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.adapter.EventFromAdapter;
import com.bepo.bean.AllGridTreeBean;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author simas
 * @describe ѡ����С��������pop
 * @time 2015-9-21 09:04:31
 * 
 */

public class SelectCommunityPop extends PopupWindow {

	Context context;
	private View View;
	private ListView lvFrom;
	TextView tvCancle;

	private EventFromAdapter<String> mEventFromAdapter;
	public static ArrayList<HashMap<String, String>> metaData = new ArrayList<HashMap<String, String>>();// Ԫ����
	public static ArrayList<String> firstData = new ArrayList<String>();// һ���˵�����

	public SelectCommunityPop(Activity context, ArrayList<HashMap<String, String>> communityList) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View = inflater.inflate(R.layout.event_from_dialog, null);

		lvFrom = (ListView) View.findViewById(R.id.lvFrom);
		tvCancle = (TextView) View.findViewById(R.id.tvCancle);
		tvCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SubmitPark2.hideBg();
				dismiss();
			}
		});
		mEventFromAdapter = new EventFromAdapter<String>(firstData, context);
		lvFrom.setAdapter(mEventFromAdapter);
		lvFrom.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, android.view.View arg1, int position, long arg3) {
				metaData.get(position);

				AllGridTreeBean mAllGridTreeBean = new AllGridTreeBean();
				mAllGridTreeBean.setCODE(metaData.get(position).get("CODE"));
				mAllGridTreeBean.setNAME(metaData.get(position).get("CAR_PARK_NAME"));
				EventBus.getDefault().post(mAllGridTreeBean);
				SubmitPark2.hideBg();
				dismiss();

			}
		});
		initData(communityList);
		// ����SelectPicPopupWindow��View
		this.setContentView(View);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.FILL_PARENT);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		// ����SelectPicPopupWindow�������嶯��Ч��
		this.setAnimationStyle(R.style.MyDialogStyle);
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// ����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
		// mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
		View.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				SubmitPark2.hideBg();
				int height = View.findViewById(R.id.event_type_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height || y > height) {
						dismiss();
						SubmitPark2.hideBg();
					}
				}
				int width = View.findViewById(R.id.event_type_layout).getLeft();
				int x = (int) event.getX();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (x < width || x > width) {
						dismiss();
						SubmitPark2.hideBg();
					}
				}

				return true;
			}
		});

	}

	private void initData(ArrayList<HashMap<String, String>> communityList) {
		metaData.clear();
		firstData.clear();
		metaData = communityList;
		for (int i = 0; i < metaData.size(); i++) {
			firstData.add(metaData.get(i).get("CAR_PARK_NAME"));
		}
		mEventFromAdapter.notifyDataSetChanged();
	}

}
