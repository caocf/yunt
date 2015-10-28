package com.zxing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
	private ImageView qrImgImageView;
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
		tvCode = (TextView) this.findViewById(R.id.tvCode);
		tvCode.setText(code);
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

		try {
			String contentString = code;
			if (!contentString.equals("")) {

				Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 600);
				qrImgImageView.setImageBitmap(qrCodeBitmap);
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
