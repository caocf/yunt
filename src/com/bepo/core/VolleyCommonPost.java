package com.bepo.core;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;

public class VolleyCommonPost extends Request<JSONObject> {
	private Map<String, String> mMap;
	private Listener<JSONObject> mListener;

	public VolleyCommonPost(String url, Listener<JSONObject> listener, ErrorListener errorListener,
			Map<String, String> map) {
		super(Request.Method.POST, url, errorListener);

		mListener = listener;
		mMap = map;
	}

	// mMap是已经按照前面的方式,设置了参数的实例
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mMap;
	}

	// 此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	// @Override
	// public Map<String, String> getHeaders() {
	// HashMap<String, String> headers = new HashMap<String, String>();
	// headers.put("Accept", "application/json");
	// headers.put("Content-Type", "application/json; charset=UTF-8");
	//
	// return headers;
	// }
	//
	@Override
	public RetryPolicy getRetryPolicy() {
		RetryPolicy retryPolicy = new DefaultRetryPolicy(50000000, 1, 1.0f);
		return retryPolicy;
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}
}