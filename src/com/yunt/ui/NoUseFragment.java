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
import com.bepo.adapter.NoPayListAdapter;

//´ýÖ§¸¶¶©µ¥
public class NoUseFragment extends Fragment {

	ListView listview;
	NoPayListAdapter mNoPayListAdapter;
	RelativeLayout rlNoData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_workflow_fragment, container, false);
		rlNoData = (RelativeLayout) view.findViewById(R.id.rlNoData);
		listview = (ListView) view.findViewById(R.id.listview);
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> noUseData = new ArrayList<HashMap<String, String>>();
		data = ((MyOrderList) getActivity()).getCarownerList();

		for (HashMap<String, String> temp : data) {
			if (temp.get("CODE_ORDER_STATUS").equals("1843")) {
				noUseData.add(temp);
			}

		}
		if (noUseData.size() > 0) {
			listview.setVisibility(View.VISIBLE);
			rlNoData.setVisibility(View.GONE);
			mNoPayListAdapter = new NoPayListAdapter(noUseData, listview, getActivity());
			listview.setAdapter(mNoPayListAdapter);
		} else {
			listview.setVisibility(View.GONE);
			rlNoData.setVisibility(View.VISIBLE);
		}

		return view;
	}

}