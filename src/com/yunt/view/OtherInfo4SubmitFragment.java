package com.yunt.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bepo.R;

public class OtherInfo4SubmitFragment extends Fragment {

	public View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.submit_other, container, false);
		return view;
	}
}