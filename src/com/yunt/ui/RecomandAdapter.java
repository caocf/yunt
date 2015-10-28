/**  
 * Project Name:Android_Car_Example  
 * File Name:RecomandAdapter.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月3日上午11:29:45  
 *  
 */

package com.yunt.ui;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bepo.R;

/**
 * ClassName:RecomandAdapter <br/>
 * Function: 显示的poi列表 <br/>
 * Date: 2015年4月3日 上午11:29:45 <br/>
 * 
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RecomandAdapter extends BaseAdapter {

	PositionEntity[] entities = new PositionEntity[] { new PositionEntity(39.908722, 116.397496, "天安门", "","010", "") };

	private List<PositionEntity> mPositionEntities;

	private Context mContext;

	public RecomandAdapter(Context context) {
		mContext = context;
		mPositionEntities = Arrays.asList(entities);

	}

	public void setPositionEntities(List<PositionEntity> entities) {
		this.mPositionEntities = entities;

	}

	@Override
	public int getCount() {

		// TODO Auto-generated method stub
		return mPositionEntities.size();
	}

	@Override
	public Object getItem(int position) {

		return mPositionEntities.get(position);
	}

	@Override
	public long getItemId(int position) {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView textView = null;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			textView = (TextView) inflater.inflate(R.layout.view_recommond, null);
		} else {
			textView = (TextView) convertView;
		}
		textView.setText(mPositionEntities.get(position).address);
		return textView;
	}

}
