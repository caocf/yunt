package com.yunt.ui;

import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;

public class AboutCompany extends BaseAct {
	private WebView webview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_company);
		initView();

	}

	private void initView() {
		getTopBar("关于我们");
		webview = (WebView) this.findViewById(R.id.webview);
		webview.setHorizontalScrollBarEnabled(false);// 水平不显示
		webview.setVerticalScrollBarEnabled(false); // 垂直不显示
		String sss = PathConfig.ADDRESS + "/m/aboutcontent/init";
		webview.loadUrl(PathConfig.ADDRESS + "/m/aboutcontent/init");

	}
}
