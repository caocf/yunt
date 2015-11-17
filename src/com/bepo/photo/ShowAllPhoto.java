package com.bepo.photo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bepo.R;

/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 * 
 */
public class ShowAllPhoto extends Activity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;

	private LinearLayout linBack;// 返回相册
	private LinearLayout linCancel;// 取消
	private LinearLayout linPreview; // 预览按钮
	private LinearLayout linOk; // 完成按钮
	private TextView tvOk;

	// 标题
	private TextView headTitle;
	private Intent intent;
	private Context mContext;
	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_show_all_photo);
		PublicWay.activityList.add(this);
		mContext = this;

		linBack = (LinearLayout) this.findViewById(R.id.linBack);
		linBack.setOnClickListener(new BackListener());

		linCancel = (LinearLayout) this.findViewById(R.id.linCancel);
		linCancel.setOnClickListener(new CancelListener());

		linPreview = (LinearLayout) this.findViewById(R.id.linPreview);
		linPreview.setOnClickListener(new PreviewListener());

		headTitle = (TextView) findViewById(R.id.showallphoto_headtitle);
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
		headTitle.setText(folderName);
		init();
		initListener();
		isShowOkBt();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mContext.unregisterReceiver(this);
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "2");
				intent.setClass(ShowAllPhoto.this, GalleryActivity.class);
				startActivity(intent);
			}
		}

	}

	private class BackListener implements OnClickListener {// 返回按钮监听
		public void onClick(View v) {
			finish();
		}

	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			finish();
		}
	}

	private void init() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);

		progressBar = (ProgressBar) findViewById(R.id.showallphoto_progressbar);
		progressBar.setVisibility(View.GONE);

		gridView = (GridView) findViewById(R.id.showallphoto_myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);

		linOk = (LinearLayout) findViewById(R.id.linOk);
		tvOk = (TextView) findViewById(R.id.tvOk);

	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
			public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, Button button) {
				if (Bimp.tempSelectBitmap.size() >= PublicWay.num && isChecked) {
					button.setVisibility(View.GONE);
					toggleButton.setChecked(false);
					Toast.makeText(ShowAllPhoto.this, Res.getString("only_choose_num"), 200).show();
					return;
				}

				if (isChecked) {
					button.setVisibility(View.VISIBLE);
					Bimp.tempSelectBitmap.add(dataList.get(position));
					tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				} else {
					button.setVisibility(View.GONE);
					Bimp.tempSelectBitmap.remove(dataList.get(position));
					tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				}
				isShowOkBt();
			}
		});

		linOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				linOk.setClickable(false);
				// if (PublicWay.photoService != null) {
				// PublicWay.selectedDataList.addAll(Bimp.tempSelectBitmap);
				// Bimp.tempSelectBitmap.clear();
				// PublicWay.photoService.onActivityResult(0, -2,
				// intent);
				// }
				// intent.setClass(mContext, SubmitPark2.class);
				// startActivity(intent);
				// Intent intent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putStringArrayList("selectedDataList",
				// selectedDataList);
				// intent.putExtras(bundle);
				// intent.setClass(ShowAllPhoto.this, UploadPhoto.class);
				// startActivity(intent);
				finish();

			}
		});

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			linPreview.setPressed(true);
			linOk.setPressed(true);
			linPreview.setClickable(true);
			linOk.setClickable(true);
			// okButton.setTextColor(Color.WHITE);
			// preview.setTextColor(Color.WHITE);
		} else {
			tvOk.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			linPreview.setPressed(false);
			linPreview.setClickable(false);
			linOk.setPressed(false);
			linOk.setClickable(false);
			// tvOk.setTextColor(Color.parseColor("#E1E0DE"));
			// linPreview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			intent.setClass(ShowAllPhoto.this, ImageFile.class);
			startActivity(intent);
		}

		return false;

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		isShowOkBt();
		super.onRestart();
	}

}
