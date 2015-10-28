package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.bean.TypeBean;
import com.yunt.ui.ParkDetailAct;

@SuppressWarnings("rawtypes")
public class MenuAdapter<T> extends CustomAdapter {
	// ����Context
	private Context context;
	private LayoutInflater inflater;
	private static List<TypeBean> data;
	private static HashMap<Integer, Boolean> isChecked;

	@SuppressLint("UseSparseArrays")
	@SuppressWarnings({ "unchecked", "static-access" })
	public MenuAdapter(ArrayList<T> data, Context context) {

		super(data, context);
		this.context = context;
		this.data = (ArrayList<TypeBean>) data;
		inflater = ((Activity) context).getLayoutInflater();
		isChecked = new HashMap<Integer, Boolean>();
		initData();

	}

	// ��ʼ��isChecked������
	private void initData() {
		for (int i = 0; i < data.size(); i++) {

			TypeBean tempInfo = (TypeBean) getItem(i);
			String isCheck = tempInfo.getFlag();

			if ("Y".equals(isCheck)) {
				isChecked.put(i, true);
			} else if ("N".equals(isCheck)) {
				isChecked.put(i, false);
			}

		}

	}

	// ��λ��������
	private void reset() {
		for (int i = 0; i < data.size(); i++) {
			isChecked.put(i, false);
		}

	}

	@SuppressWarnings("static-access")
	public void setData(ArrayList<TypeBean> data) {
		this.data = data;
	}

	// ����
	@Override
	public int getCount() {
		return data.size();
	}

	// λ��
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View cellView = convertView;
		ViewHolder viewCache;
		if (cellView == null) {
			cellView = inflater.inflate(R.layout.menu_items, null);
			viewCache = new ViewHolder();

			viewCache.tv_name = (TextView) cellView.findViewById(R.id.tv_name);
			viewCache.iv_icon = (ImageView) cellView.findViewById(R.id.iv_icon);

			cellView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) cellView.getTag();
		}

		TypeBean appData = null;

		appData = (TypeBean) getItem(position);
		// String ss = appData.getFlag();
		boolean sss = isChecked.get(position);

		if (sss == true) {
			viewCache.tv_name.setBackgroundResource(R.drawable.slidingmenu_btbg);
		} else {
			viewCache.tv_name.setBackgroundResource(R.drawable.slidingmenu_btbg);
		}

		viewCache.tv_name.setText(appData.getName());
		final String typeName = appData.getName();

		if (typeName.equals("�ҵĳ�λ")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_location);
		} else if (typeName.equals("��ݹ��ѯ")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_search);
		} else if (typeName.equals("��ݵ���ѯ")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_search);
		} else if (typeName.equals("ϵͳ����")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_setting);
		} else if (typeName.equals("��������")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_about);
		} else if (typeName.equals("��ҵ��˾��ѯ")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_store);
		} else if (typeName.equals("��ҵ��λ��ѯ")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_flashlight);
		} else if (typeName.equals("�˳�")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_flashlight);
		}

		// viewCache.tv_name.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// reset();
		// isChecked.put(position, true);
		//
		// if (typeName.equals("��ݵ��ѯ")) {
		// Intent intent = new Intent(context, EPDeliverypointActivity.class);
		// intent.putExtra("flag", "0");
		// context.startActivity(intent);
		// } else if (typeName.equals("��ݹ��ѯ")) {
		// Intent intent = new Intent(context, EPDeliverypointActivity.class);
		// intent.putExtra("flag", "1");
		// context.startActivity(intent);
		// } else if (typeName.equals("�˳�")) {
		// Intent intent = new Intent(context, OrderDialog.class);
		// context.startActivity(intent);
		//
		// }
		//
		// };
		// });

		cellView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reset();
				isChecked.put(position, true);

				if (typeName.equals("��ݵ��ѯ")) {
					Intent intent = new Intent(context, ParkDetailAct.class);
					intent.putExtra("flag", "0");
					context.startActivity(intent);
				}
			}
		});

		return cellView;

	}

	public static Object getData(int position) {
		return data.get(position);
	}

	public final class ViewHolder {

		public TextView tv_name;
		public ImageView iv_icon;

	}

}