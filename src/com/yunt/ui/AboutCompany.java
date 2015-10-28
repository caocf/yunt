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
		getTopBar("��������");
		webview = (WebView) this.findViewById(R.id.webview);
		webview.setHorizontalScrollBarEnabled(false);// ˮƽ����ʾ
		webview.setVerticalScrollBarEnabled(false); // ��ֱ����ʾ
		String sss = PathConfig.ADDRESS + "/m/aboutcontent/init";
		webview.loadUrl(PathConfig.ADDRESS + "/m/aboutcontent/init");

	}
}
