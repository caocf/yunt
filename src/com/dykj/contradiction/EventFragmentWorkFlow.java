package com.dykj.contradiction;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bepo.R;
import com.bepo.adapter.EventWorkAdapter;

public class EventFragmentWorkFlow extends Fragment {

	ListView listview;
	EventWorkAdapter mEventWorkAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_workflow_fragment, container, false);
		listview = (ListView) view.findViewById(R.id.listview);
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
//		data = ((EventDetail) getActivity()).getOpinions();
//
//		mEventWorkAdapter = new EventWorkAdapter(data, listview, getActivity());
//		listview.setAdapter(mEventWorkAdapter);
		
		
		return view;
	}

}