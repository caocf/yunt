package com.bepo.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bepo.R;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.yunt.ui.LoginActivity;

public class SampleActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private RelativeLayout leftDrawer;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable drawerArrow;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true); // 给左上角图标的左边加上一个返回的图标
		ab.setHomeButtonEnabled(true); // 决定左上角的图标是否可以点击
		ab.setTitle("首页");

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
		mDrawerList = (ListView) findViewById(R.id.navdrawer);

		drawerArrow = new DrawerArrowDrawable(this) {
			@Override
			public boolean isLayoutRtl() {
				return false;
			}
		};

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();

		String[] values = new String[] { "基础数据", "矛盾联动化解", "GPS定位上传" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, values);
		mDrawerList.setAdapter(adapter);

		mDrawerLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				ActionBar ab = getActionBar();
				ab.setTitle("首页");
				return false;
			}
		});

		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					mDrawerToggle.syncState();
					Intent intent = new Intent(SampleActivity.this, LoginActivity.class);
					SampleActivity.this.startActivity(intent);

					break;

				}

			}
		});
	}

	@SuppressLint("NewApi") @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(leftDrawer)) {
				mDrawerLayout.closeDrawer(leftDrawer);
				ActionBar ab = getActionBar();
				ab.setTitle("首页");
			} else {
				mDrawerLayout.openDrawer(leftDrawer);
				ActionBar ab = getActionBar();
				ab.setTitle("掌上网格");

			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);

	}
}
