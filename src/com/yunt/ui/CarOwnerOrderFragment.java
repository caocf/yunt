package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bepo.R;
import com.bepo.adapter.CarOwnerListAdapter;

public class CarOwnerOrderFragment extends Fragment {

	ListView listview;
	CarOwnerListAdapter mCarOwnerListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_workflow_fragment, container, false);
		listview = (ListView) view.findViewById(R.id.listview);
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		data = ((MyOrderList) getActivity()).getCarownerList();
		mCarOwnerListAdapter = new CarOwnerListAdapter(data, listview, getActivity());
		listview.setAdapter(mCarOwnerListAdapter);

		return view;
	}

}