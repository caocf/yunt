package com.bepo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bepo.R;
import com.bepo.ui.MainAct;
import com.dykj.contradiction.EventAccept;
import com.dykj.contradiction.EventQueryAll;
import com.dykj.contradiction.EventQueryCancel;
import com.dykj.contradiction.EventQueryHistrory;
import com.dykj.contradiction.EventQueryTodo;
import com.dykj.diary.DiaryList;
import com.dykj.diary.DiarySubmit;
import com.finddreams.graygridView.BaseViewHolder;

public class MyGridAdapter extends BaseAdapter {
	private Context mContext;
	public String[] img_text = { "事件受理", "事件待办", "事件已办", "综合查询", "事件注销", "轨迹上传", "民情日志" ,"日志提交"};
	public int[] imgs = { R.drawable.home_event, R.drawable.home_jichuxinxi, R.drawable.home_xinxigongxiang,
			R.drawable.home_xunchaguiji, R.drawable.home_tongjifenxi, R.drawable.home_mingqingrizhi,
			R.drawable.home_jujiayanglao, R.drawable.home_dahonghua, R.drawable.home_qita };

	public MyGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		final ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		iv.setBackgroundResource(imgs[position]);
		tv.setText(img_text[position]);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (position) {
				case 0:
					Intent intent0 = new Intent(mContext, EventAccept.class);
					mContext.startActivity(intent0);
					break;
				case 1:
					Intent intent1 = new Intent(mContext, EventQueryTodo.class);
					mContext.startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(mContext, EventQueryHistrory.class);
					mContext.startActivity(intent2);
					break;

				case 3:
					Intent intent3 = new Intent(mContext, EventQueryAll.class);
					mContext.startActivity(intent3);
					break;
				case 4:
					Intent intent4 = new Intent(mContext, EventQueryCancel.class);
					mContext.startActivity(intent4);
					break;

				case 5:
					MainAct.getGPS();
					break;
				case 6:
					Intent intent6 = new Intent(mContext, DiaryList.class);
					mContext.startActivity(intent6);
					break;
				case 7:
					Intent intent7 = new Intent(mContext, DiarySubmit.class);
					mContext.startActivity(intent7);
					break;
					
					
					
				default:
					break;
				}

			}
		});
		return convertView;
	}
}
