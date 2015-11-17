package com.bepo.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bepo.R;
import com.yunt.ui.SubmitPark2;

/**
 * �������Ҫ������������ʾ����ͼƬ���ļ���
 */
public class ImageFile extends Activity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_image_file);
		PublicWay.activityList.add(this);
		mContext = this;

		bt_cancel = (Button) findViewById(R.id.cancel);
		bt_cancel.setOnClickListener(new CancelListener());

		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		TextView textView = (TextView) findViewById(R.id.headerTitle);
		textView.setText("ѡ�����");
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// ȡ����ť�ļ���
		public void onClick(View v) {
			// ���ѡ���ͼƬ
			Bimp.tempSelectBitmap.clear();
			finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}

		return true;
	}

}
