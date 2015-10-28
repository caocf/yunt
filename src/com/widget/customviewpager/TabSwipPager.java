package com.widget.customviewpager;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabSwipPager {

	private CustomViewManager customView;

	private Context context;
	private LinearLayout parentView, llTab;
	private TextView tv;
	private FragmentManager fm;
	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;

	public TabSwipPager(Context context, FragmentManager fm, LinearLayout parentView) {
		this.context = context;
		this.fm = fm;
		this.parentView = parentView;
	}

	public boolean setFragmentList(ArrayList<Fragment> fragmentsList, String[] tags) {
		if (tags.length != fragmentsList.size()) {
			return false;
		}

		customView = new CustomViewManager(context, tags);

		llTab = customView.getTabView();
		tv = customView.getTextView();
		viewPager = customView.getViewPager();

		pagerAdapter = new PagerAdapter(context, fm, fragmentsList, viewPager, llTab, tv);
		viewPager.setAdapter(pagerAdapter);

		parentView.addView(customView.getCustomTabView(), new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		return true;
	}

}
