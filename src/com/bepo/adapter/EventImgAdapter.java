package com.bepo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.amap.api.mapcore.util.r;
import com.bepo.R;
import com.bepo.core.PathConfig;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EventImgAdapter<T> extends BaseAdapter {
	private List<Map<String, Object>> data;
	public Context context;
	public LayoutInflater inflater;

	public EventImgAdapter(ArrayList<Map<String, Object>> data, Context context) {
		this.data = data;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		ViewHolder viewCache;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.event_img, null);
			viewCache = new ViewHolder();
			viewCache.image = (ImageView) rowView.findViewById(R.id.image);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewHolder) rowView.getTag();

		}
		String imageUrl =  PathConfig.ADDRESS + "uploads/picture/" + data.get(position).get("PATH");
		ImageLoader.getInstance().displayImage(imageUrl, viewCache.image);
		return rowView;
	}

	private class ViewHolder {

		public ImageView image;

	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

}
