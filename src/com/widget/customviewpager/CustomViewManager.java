package com.widget.customviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bepo.R;

public class CustomViewManager {
	private Context context;
	private LinearLayout llCustomTabView, llTab;
	private ViewPager viewPager;
	private String[] tags;
	private TextView tv;

	public CustomViewManager(Context context, String[] tags) {
		this.context = context;
		this.tags = tags;
		// 获取全部布局
		llCustomTabView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.custom_tab_view, null);
		// 拿到存放TAB的布局
		llTab = (LinearLayout) llCustomTabView.findViewById(R.id.llTab);
		viewPager = (ViewPager) llCustomTabView.findViewById(R.id.pager);

		createCustomView();
	}

	public LinearLayout getCustomTabView() {
		return llCustomTabView;
	}

	public LinearLayout getTabView() {
		return llTab;
	}

	public TextView getTextView() {
		return tv;
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	private void createCustomView() {
		// 添加TABS
		for (int i = 0; i < tags.length; i++) {
			RelativeLayout tab = new RelativeLayout(context);
			tab.setId(i);
			TextView tv = new TextView(context);
			tv.setTextSize(16);
			tv.setId(i);
			tv.setText(tags[i]);
			
			if (i == 0) {
				tab.setBackgroundResource(R.drawable.bg_view_pager_scroll_selected);
				tv.setTextColor(context.getResources().getColor(R.color.tab_text));
			} else {
				tab.setBackgroundResource(R.drawable.bg_view_pager_scroll_unselect);
				tv.setTextColor(context.getResources().getColor(R.color.tab_text));
			}

			RelativeLayout.LayoutParams tvlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			tvlp.addRule(RelativeLayout.CENTER_IN_PARENT);
			tab.addView(tv, tvlp);

			LinearLayout.LayoutParams tablp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
			tablp.weight = 1;
			llTab.addView(tab, tablp);

		}
	}
}