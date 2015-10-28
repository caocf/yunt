package com.bepo.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bepo.R;
import com.bepo.adapter.MyGridAdapter;
import com.bepo.core.BaseAct;
import com.bepo.view.BarChart3D01View;
import com.dykj.trajectory.GpsFragment;
import com.finddreams.graygridView.MyGridView;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class MainAct extends BaseAct {
	private MyGridView gridview;
	private RelativeLayout rlChart;
	// SplineChart01View chart = null;
	BarChart3D01View chart = null;
	public RelativeLayout rlAll;
	public LinearLayout linAll;
	static FragmentManager mFragmentManager;
	public static long firstTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_jiugongge);

		initView();
		noBack("掌上网格");
	}

	private void initView() {
		linAll = (LinearLayout) findViewById(R.id.linAll);
		rlAll = (RelativeLayout) findViewById(R.id.rlAll);
		rlChart = (RelativeLayout) findViewById(R.id.rlChart);
		chart = (BarChart3D01View) findViewById(R.id.BarChart3D01View);
		// chart = (SplineChart01View) findViewById(R.id.SplineChart01View);
		gridview = (MyGridView) findViewById(R.id.gridview);
		gridview.setAdapter(new MyGridAdapter(this));
		mFragmentManager = getSupportFragmentManager();

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {// 如果两次按键时间间隔大于800毫秒，则不退出
				ToastUtils.showSuperToastAlert(this, "再次点击退出应用");
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				System.exit(0);
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	public static void getGPS() {

		GpsFragment fragment1 = new GpsFragment();
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_down_roate_in, R.anim.pop_down_roate_out,
				R.anim.pop_down_roate_in, R.anim.pop_down_roate_out);
		fragmentTransaction.add(R.id.rlAll, fragment1);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

	}
}
