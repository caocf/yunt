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
	// 定义Context
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

	// 初始化isChecked的数据
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

	// 复位所有类型
	private void reset() {
		for (int i = 0; i < data.size(); i++) {
			isChecked.put(i, false);
		}

	}

	@SuppressWarnings("static-access")
	public void setData(ArrayList<TypeBean> data) {
		this.data = data;
	}

	// 个数
	@Override
	public int getCount() {
		return data.size();
	}

	// 位置
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

		if (typeName.equals("我的车位")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_location);
		} else if (typeName.equals("快递柜查询")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_search);
		} else if (typeName.equals("快递单查询")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_search);
		} else if (typeName.equals("系统设置")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_setting);
		} else if (typeName.equals("关于我们")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_about);
		} else if (typeName.equals("物业公司查询")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_store);
		} else if (typeName.equals("物业单位查询")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_flashlight);
		} else if (typeName.equals("退出")) {
			viewCache.iv_icon.setBackgroundResource(R.drawable.ep_flashlight);
		}

		// viewCache.tv_name.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// reset();
		// isChecked.put(position, true);
		//
		// if (typeName.equals("快递点查询")) {
		// Intent intent = new Intent(context, EPDeliverypointActivity.class);
		// intent.putExtra("flag", "0");
		// context.startActivity(intent);
		// } else if (typeName.equals("快递柜查询")) {
		// Intent intent = new Intent(context, EPDeliverypointActivity.class);
		// intent.putExtra("flag", "1");
		// context.startActivity(intent);
		// } else if (typeName.equals("退出")) {
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

				if (typeName.equals("快递点查询")) {
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