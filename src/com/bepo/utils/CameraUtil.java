package com.bepo.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

public class CameraUtil {

	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// ֻ����,��������
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 240f;//
		float ww = 360f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// ���ò�����
		newOpts.inPreferredConfig = Config.RGB_565;
		// newOpts.inPreferredConfig = Config.ARGB_8888;//��ģʽ��Ĭ�ϵ�,�ɲ���
		newOpts.inPurgeable = true;// ͬʱ���òŻ���Ч
		newOpts.inInputShareable = true;// ����ϵͳ�ڴ治��ʱ��ͼƬ�Զ�������
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//ԭ���ķ������������������ͼ���ж���ѹ��
		// ��ʵ����Ч��,��Ҿ��ܳ���
		return bitmap;
	}

	// ��byteת����Bitmap
	public static Bitmap getPicFromBytes(byte[] bytes) {

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// ֻ����,��������
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 240f;//
		float ww = 360f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// ���ò�����
		newOpts.inPreferredConfig = Config.ARGB_8888;// ��ģʽ��Ĭ�ϵ�,�ɲ���
		newOpts.inPurgeable = true;// ͬʱ���òŻ���Ч
		newOpts.inInputShareable = true;// ����ϵͳ�ڴ治��ʱ��ͼƬ�Զ�������
		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, newOpts);
		// return compressBmpFromBmp(bitmap);//ԭ���ķ������������������ͼ���ж���ѹ��
		// ��ʵ����Ч��,��Ҿ��ܳ���
		return bitmap;
	}

	// Bitmapת����byte
	static public byte[] BitmapToBytes(Bitmap bm, int percent) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, percent, baos);
		return baos.toByteArray();
	}

	// ��byteת����Bitmap
	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	// ������������Ϊ�ֽ��ڴ���������ֽ�����
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	public static String byte2hex(byte[] b) // ������ת�ַ���

	{

		StringBuffer sb = new StringBuffer();
		String stmp = "";

		for (int n = 0; n < b.length; n++) {

			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

			if (stmp.length() == 1) {
				sb.append("0" + stmp);
			} else {
				sb.append(stmp);
			}
		}
		return sb.toString();
	}

}
