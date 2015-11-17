package com.bepo.photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.CameraUtil;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class UploadPhotoService extends Service {

	ArrayList<String> list = new ArrayList<String>();
	private Bitmap myBitmap;
	private byte[] myByte;

	@Override
	public void onCreate() {
		super.onCreate();
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			myBitmap = Bimp.tempSelectBitmap.get(i).getBitmap();
			myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
			list.add(CameraUtil.byte2hex(myByte));
		}
		for (int i = 0; i < list.size(); i++) {
			submitPic(list.get(i));
		}
		// PhotoAsyncTask pa = new PhotoAsyncTask();
		// pa.equals(list);

	}

	private void submitPic(String s) {

		String picUrl = PathConfig.ADDRESS + "/zcw/base/bupload/uploadApp?ukey=" + PathConfig.ukey;
		Map<String, String> params = new HashMap<String, String>();
		params.put("picString", s);

		Request<JSONObject> request = new VolleyCommonPost(picUrl, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "ÉÏ´«Í¼Æ¬Ê§°Ü");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);
	}

	class PhotoAsyncTask extends AsyncTask<ArrayList<String>, Void, Void> {

		@Override
		protected Void doInBackground(ArrayList<String>... params) {
			for (int i = 0; i < params.length; i++) {
				submitPic(list.get(i));
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);

	}
}
