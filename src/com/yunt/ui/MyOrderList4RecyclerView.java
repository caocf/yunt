package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;

public class MyOrderList4RecyclerView extends BaseAct {

	RecyclerView mRecyclerView;
	LinearLayoutManager mLayoutManager;
	MyAdapter mAdapter;
	ArrayList<HashMap<String, String>> CarownerList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_order_list_recylerview);
		getTopBar("�ҵĶ���");
		initCarownerList();
	}

	private void initView() {
		mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
		// ����Ĭ�ϵ�����LayoutManager
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		// �������ȷ��ÿ��item�ĸ߶��ǹ̶��ģ��������ѡ������������
		mRecyclerView.setHasFixedSize(true);
		// ����������Adapter
		mAdapter = new MyAdapter(CarownerList);
		mRecyclerView.setAdapter(mAdapter);
	}

	// ���⵽�ĳ�λ
	private void initCarownerList() {

		String url = PathConfig.ADDRESS + "/trad/order/carowner/list";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				dismissDialog();
				CarownerList = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});
				initView();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
		ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

		public MyAdapter(ArrayList<HashMap<String, String>> datas) {
			this.datas = datas;
		}

		// ������View����LayoutManager������
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.carowner_order_items,
					viewGroup, false);
			ViewHolder vh = new ViewHolder(view);
			return vh;
		}

		// �������������а󶨵Ĳ���
		@Override
		public void onBindViewHolder(ViewHolder viewHolder, int position) {
			// viewHolder.mTextView.setText(datas.);s
		}

		// ��ȡ���ݵ�����
		@Override
		public int getItemCount() {
			return datas.size();
		}

		// �Զ����ViewHolder������ÿ��Item�ĵ����н���Ԫ��
		public class ViewHolder extends RecyclerView.ViewHolder {
			public TextView mTextView;

			public ViewHolder(View view) {
				super(view);
				mTextView = (TextView) view.findViewById(R.id.text);
			}
		}
	}
}
