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

		// ������
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
		getTopBar("�ύ�ɹ�");

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

				wechatShare(1);// ����΢������Ȧ
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		linShareFriend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wechatShare(0);// ����΢�ź���
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

	}

	private void initShare() {
		// ʵ����
		wxApi = WXAPIFactory.createWXAPI(this, PayID.APP_ID);
		wxApi.registerApp(PayID.APP_ID);

	}

	/**
	 * ΢�ŷ��� ��������ṩһ��������ҳ��ʾ����������ο�����ʾ�����룩 (0:����΢�ź��ѣ�1������΢������Ȧ)
	 */
	private void wechatShare(int flag) {
		initShare();
		WXWebpageObject webpage = new WXWebpageObject();

		 webpage.webpageUrl = "http://www.sharecar.cn/zcw/m/controduce/init";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "��ͨ�� ��ͣ�� ������һ����λ ,����ͣ��ͣ���о�������!";
		msg.description = "����û�ù���ͣ��������ʹ��,һ����������ͣ��ͣ�ĳ���!";
		// �����滻һ���Լ��������ͼƬ��Դ
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.redyf);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

}
