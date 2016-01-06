package com.zxing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.google.zxing.WriterException;

public class EPQRAct extends BaseAct implements OnClickListener {

	private TextView tvClose;
	private TextView tvCode;
	private ImageView qrImgImageView, barImgImageView;
	private int CurrentTemp;

	public String phone;
	public String code;

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ep_qr);

		CurrentTemp = LightnessControl.GetLightness(this);
		if (LightnessControl.isAutoBrightness(this)) {
			LightnessControl.stopAutoBrightness(this);
		}
		LightnessControl.SetLightness(this, 200);

		Intent intent = this.getIntent();

		if (intent != null && intent.getStringExtra("code") != null) {
			code = intent.getStringExtra("code");
		}
		String regex = "(.{4})";
		String codetext = code.replaceAll(regex, "$1  ");
		tvCode = (TextView) this.findViewById(R.id.tvCode);
		tvCode.setText(codetext);

		tvClose = (TextView) this.findViewById(R.id.tvClose);
		tvClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LightnessControl.SetLightness(EPQRAct.this, CurrentTemp);
				LightnessControl.startAutoBrightness(EPQRAct.this);
				finish();
				overridePendingTransition(0, R.anim.push_bottom_out);
			}
		});

		qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
		barImgImageView = (ImageView) this.findViewById(R.id.iv_bar_image);

		try {
			String contentString = code;
			if (!contentString.equals("")) {

				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				// int screenWidth = dm.widthPixels;
				// int screenHeight = dm.heightPixels;
				// Log.e("³ß´ç", screenWidth + " " + screenHeight);

				// ¶þÎ¬Âë
				Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 800);
				qrImgImageView.setImageBitmap(qrCodeBitmap);

				// ÌõÐÎÂë
				Bitmap barCodeBitmap = EncodingHandler.creatBarcode(this, contentString, 1000, 400, false);
				barImgImageView.setImageBitmap(barCodeBitmap);

			} else {
				Toast.makeText(EPQRAct.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
			}

		} catch (WriterException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LightnessControl.SetLightness(EPQRAct.this, CurrentTemp);
			LightnessControl.startAutoBrightness(EPQRAct.this);
		}
		return super.onKeyUp(keyCode, event);
	}
}
