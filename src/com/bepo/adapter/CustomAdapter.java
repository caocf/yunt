package com.bepo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CustomAdapter<T> extends BaseAdapter {

	public ArrayList<T> data;
	public Context context;
	public LayoutInflater inflater;

	public CustomAdapter(ArrayList<T> data, Context context) {
		this.data = data;
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addData(List<T> data) {
		int size = data.size();
		for (int i = 0; i < size; i++) {
			this.data.add(data.get(i));
		}
		this.notifyDataSetChanged();
	}

	public void addData(ArrayList<T> data) {
		int size = data.size();
		for (int i = 0; i < size; i++) {
			this.data.add(data.get(i));
		}
		this.notifyDataSetChanged();
	}

	public void insertData(ArrayList<T> data) {
		int size = data.size();
		for (int i = size - 1; i > -1; i--) {
			this.data.add(0, data.get(i));
		}
		this.notifyDataSetChanged();
	}

	public void insertData(ArrayList<T> data, int indexID) {
		int size = data.size();

		if (this.data.size() >= indexID) {
			for (int i = size - 1; i > -1; i--) {
				this.data.add(indexID, data.get(i));
			}
		} else {
			for (int i = size - 1; i > -1; i--) {
				this.data.add(0, data.get(i));
			}
		}

		this.notifyDataSetChanged();
	}

	public void clearData() {
		this.data.clear();
	}

	public void clearData(int indexID) {
		// Log.i("MyLog",this.data.size()+"");
		if (this.data.size() > indexID) {
			while (indexID < this.data.size()) {
				this.data.remove(indexID);
			}
		}
		// Log.i("MyLog",this.data.size()+"");

		// this.data.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
