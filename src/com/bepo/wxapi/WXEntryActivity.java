package com.bepo.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.bepo.pay.PayID;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		api = WXAPIFactory.createWXAPI(this, PayID.APP_ID);
		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			ToastUtils.showSuperToastAlertGreen(getApplicationContext(), "分享成功");
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			ToastUtils.showSuperToastAlertGreen(getApplicationContext(), "取消分享");
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			ToastUtils.showSuperToastAlertGreen(getApplicationContext(), "您分享的内容被微信拒绝");
			break;
		}
		finish();
	}
}
