package com.bepo.photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bepo.R;
import com.github.johnpersano.supertoasts.util.ToastUtils;

/**
 * 这个是进入相册显示所有图片的界面
 */
public class AlbumActivity extends Activity {

	private Context mContext;
	private Intent intent;

	private GridView gridView;// 显示手机里的所有图片的列表控件
	private TextView tv; // 当手机里没有图片时，提示用户没有图片的控件
	private AlbumGridViewAdapter gridImageAdapter;// gridView的adapter

	private LinearLayout linBack;// 返回相册
	private LinearLayout linCancel;// 取消

	private LinearLayout linPreview; // 预览按钮
	private LinearLayout linOk; // 完成按钮

	private TextView tvOk, tvYuanlan;

	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		// PublicWay.activityList.add(this);
		mContext = this;
		// 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
		init();
		initListener();
		// 这个函数主要用来控制预览和完成按钮的状态
		isShowOkBt();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mContext.unregisterReceiver(this);
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	// 预览按钮的监听
	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "1");
				intent.setClass(AlbumActivity.this, GalleryActivity.class);
				startActivityForResult(intent, 0);
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			finish();
			break;
		}
	}

	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			finish();
		}

	}

	// 返回按钮监听
	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			intent.setClass(AlbumActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}

	// 取消按钮的监听
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			finish();
		}
	}

	// 初始化，给一些对象赋值
	private void init() {

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		// 得到手机图片集
		contentList = helper.getImagesBucketList(false);

		// 得到每一个相册的的图片list
		dataList = new ArrayList<ImageItem>();
		// for (int i = 0; i < contentList.size(); i++) {
		// dataList.addAll(contentList.get(i).imageList);
		// }
		// 得到相机那个相册
		for (int i = 0; i < contentList.size(); i++) {
			if (contentList.get(i).bucketName.toString().equals("Camera")) {
				dataList = (ArrayList<ImageItem>) AlbumActivity.contentList.get(i).imageList;
				Collections.reverse(dataList);
			}
		}

		linBack = (LinearLayout) this.findViewById(R.id.linBack);
		linBack.setOnClickListener(new BackListener());

		linCancel = (LinearLayout) this.findViewById(R.id.linCancel);
		linCancel.setOnClickListener(new CancelListener());

		linPreview = (LinearLayout) this.findViewById(R.id.linPreview);
		linPreview.setOnClickListener(new PreviewListener());
		tvYuanlan = (TextView) findViewById(R.id.tvYuanlan);

		intent = getIntent();
		Bundle bundle = intent.getExtras();

		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);

		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);

		linOk = (LinearLayout) findViewById(R.id.linOk);
		tvOk = (TextView) findViewById(R.id.tvOk);

		tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked,
					Button chooseBt) {
				if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
					toggleButton.setChecked(false);
					chooseBt.setVisibility(View.GONE);
					if (!removeOneData(dataList.get(position))) {
						ToastUtils.showSuperToastAlert(AlbumActivity.this, "超出可选图片张数");
					}
					return;
				}
				if (isChecked) {
					chooseBt.setVisibility(View.VISIBLE);
					Bimp.tempSelectBitmap.add(dataList.get(position));
					tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				} else {
					Bimp.tempSelectBitmap.remove(dataList.get(position));
					chooseBt.setVisibility(View.GONE);
					tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				}
				isShowOkBt();
			}
		});

		linOk.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
		if (Bimp.tempSelectBitmap.contains(imageItem)) {
			Bimp.tempSelectBitmap.remove(imageItem);
			tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			return true;
		}
		return false;
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");

			linPreview.setPressed(true);
			linPreview.setClickable(true);
			tvYuanlan.setTextColor(Color.BLACK);

			linOk.setPressed(true);
			linOk.setClickable(true);
			tvOk.setTextColor(Color.WHITE);

		} else {
			tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");

			linPreview.setPressed(false);
			linPreview.setClickable(false);
			tvYuanlan.setTextColor(Color.parseColor("#E2E2E2"));

			linOk.setPressed(false);
			linOk.setClickable(false);
			tvOk.setTextColor(Color.parseColor("#82BAF0"));

		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bimp.tempSelectBitmap.clear();
			finish();
		}
		return false;

	}

	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}
}
