package com.yunt.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.pay.PayID;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class SubmitParkSucess extends BaseAct {

	RelativeLayout rlShare, rlMain;
	private IWXAPI wxApi;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submitpark_sucess);

		// 分享功能
		rlMain = (RelativeLayout) this.findViewById(R.id.rlMain);
		rlShare = (RelativeLayout) this.findViewById(R.id.rlShare);
		rlShare.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				ll_popup.startAnimation(AnimationUtils.loadAnimation(SubmitParkSucess.this,
						R.anim.abc_slide_in_bottom));
				pop.showAtLocation(rlMain, Gravity.BOTTOM, 0, 0);
			}
		});
		initPop();
		getTopBar("提交成功");

	}

	private void initPop() {
		pop = new PopupWindow(SubmitParkSucess.this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);

		LinearLayout linShareCircle = (LinearLayout) view.findViewById(R.id.linShareCircle);
		LinearLayout linShareFriend = (LinearLayout) view.findViewById(R.id.linShareFriend);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		linShareCircle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				wechatShare(1);// 分享到微信朋友圈
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		linShareFriend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wechatShare(0);// 分享到微信好友
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

	}

	private void initShare() {
		// 实例化
		wxApi = WXAPIFactory.createWXAPI(this, PayID.APP_ID);
		wxApi.registerApp(PayID.APP_ID);

	}

	/**
	 * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码） (0:分享到微信好友，1：分享到微信朋友圈)
	 */
	private void wechatShare(int flag) {
		initShare();
		WXWebpageObject webpage = new WXWebpageObject();

		 webpage.webpageUrl = "http://www.sharecar.cn/zcw/m/controduce/init";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "我通过 云停宝 发布了一个车位 ,用云停宝停车感觉棒棒的!";
		msg.description = "邀请没用过云停宝的朋友使用,一起来体验想停就停的畅快!";
		// 这里替换一张自己工程里的图片资源
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.redyf);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

}
