package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bepo.R;
import com.bepo.adapter.CarOwnerListAdapter;

public class CarOwnerOrderFragment extends Fragment {
	// È«²¿¶©µ¥
	ListView listview;
	CarOwnerListAdapter mCarOwnerListAdapter;

	RelativeLayout rlNoData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_workflow_fragment, container, false);
		rlNoData = (RelativeLayout) view.findViewById(R.id.rlNoData);
		listview = (ListView) view.findViewById(R.id.listview);
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		data = ((MyOrderList) getActivity()).getCarownerList();

		if (data.size() > 0) {
			rlNoData.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			mCarOwnerListAdapter = new CarOwnerListAdapter(data, listview, getActivity());
			listview.setAdapter(mCarOwnerListAdapter);
		} else {
			rlNoData.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}

		return view;
	}

}