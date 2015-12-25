package com.yunt.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.LinearLayout;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.widget.customviewpager.TabSwipPager;

public class RentTime extends BaseAct {

	private LinearLayout llTabSwipPager;
	private TabSwipPager tabSwipPager;

	private ArrayList<Fragment> fragmentsList;
	private String[] tags;
	public static Boolean isBusiness = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_order_list);
		getTopBar("选择租赁时间");

		if (getIntent().getExtras().get("isBusiness").toString().equals("0")) {
			isBusiness = true;
		}

		initView();
	}

	private void initView() {

		tags = new String[] { " 时租 ", " 月租 " };
		fragmentsList = new ArrayList<Fragment>();
		fragmentsList.add(new HourFragment());
		fragmentsList.add(new MonthFragment());

		llTabSwipPager = (LinearLayout) findViewById(R.id.llTabSwipPager);
		tabSwipPager = new TabSwipPager(RentTime.this, getSupportFragmentManager(), llTabSwipPager);
		if (!tabSwipPager.setFragmentList(fragmentsList, tags)) {
			finish();
		}
	}

}
