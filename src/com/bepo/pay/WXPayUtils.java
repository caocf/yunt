package com.bepo.pay;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.bepo.core.ApplicationController;
import com.bepo.utils.MyTextUtils;
import com.tencent.mm.sdk.modelpay.PayReq;

public class WXPayUtils {

	static PayReq req = new PayReq();
	private StringBuffer sb;
	Map<String, String> resultunifiedorder;

	String out_trade_no;
	String total_fee;

	// app֧������Ԥ֧������
	public WXPayUtils(String out_trade_no, String total_fee) {
		this.out_trade_no = out_trade_no;
		float temp = Float.parseFloat(total_fee) * 100;
		this.total_fee = (int) temp + "";

		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
	}

	// �̳� AsyncTask, ��ִ̨�з�������
	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {
		private ProgressDialog dialog;

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();// ��ȡ������ز���
			Log.e("������ز���====== ", entity);
			byte[] buf = WeixinUtil.httpPost(url, entity);
			String content = new String(buf);
			Log.e("Ԥ֧������ص�=====", content);
			Map<String, String> xml = decodeXml(content);
			return xml;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			resultunifiedorder = result;

			genPayReq();// ����Ԥ֧�������ɹ���,����ǩ������,����΢��֧������
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

	}

	// ��ȡԤ֧���������ز���
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();
		try {

			String nonceStr = genNonceStr();// ���������
			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", PayID.APP_ID));// ΢����ȨID
			packageParams.add(new BasicNameValuePair("body", "��ͣ������_" + out_trade_no));
			packageParams.add(new BasicNameValuePair("mch_id", PayID.MCH_ID));// �̻���
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", PayID.NOTIFY_URL));// ֪ͨ��ַ
			packageParams.add(new BasicNameValuePair("out_trade_no", out_trade_no));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", total_fee));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			// �״�ǩ������������
			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
			// ��װ�� xml ����������
			String xmlstring = toXml(packageParams);
			//
			xmlstring = new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");
			return xmlstring;

		} catch (Exception e) {
			return null;
		}

	}

	// �û��ٴ�ǩ��prepay_id
	private void genPayReq() {

		req.appId = PayID.APP_ID;
		req.partnerId = PayID.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		// �ٴ�ǩ��
		req.sign = genAppSign(signParams);
		Log.e("�ٴ�ǩ������µ�����", signParams.toString());

		// ��������
		ApplicationController.getInstance().sendPayReq(req);

	}

	// ===================================������ҵ���޹�,Ϊ��ط���==================================

	// �״ν����ݽ���ǩ������
	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(PayID.API_KEY);
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("�״�ǩ��������", packageSign);
		return packageSign;
	}

	// �ٴν����ݽ���ǩ������
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(PayID.API_KEY);

		// this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("�ٴ�ǩ��������", appSign);
		return appSign;
	}

	// ��Ԥ֧����ز�����װ�� xml
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("��Ԥ֧����ز�����װ�� xml,", sb.toString());
		return sb.toString();
	}

	// ����Ԥ֧���Ļص�
	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("xml".equals(nodeName) == false) {
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("����Ԥ֧���ص�ʧ��", e.toString());
		}
		return null;

	}

	// ����ʱ���
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	// �����
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
}
