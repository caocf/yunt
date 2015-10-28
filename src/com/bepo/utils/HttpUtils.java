package com.bepo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	/*
	 * java原生提供的post方式 Function : 发送Post请求到服务器 Param : params请求体内容，encode编码格式
	 */
	public static String submitPostData(Map<String, String> params, String urlStr) {

		byte[] data = getRequestData(params, "utf-8").toString().getBytes();// 获得请求体
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(3000); // 设置连接超时时间
			httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
			httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
			httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
			httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存
			// 设置请求体的类型是文本类型
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 设置请求体的长度
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
			// 获得输出流，向服务器写入数据
			// 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
			// 所以在开发中不调用上述的connect()也可以)。
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);

			int response = httpURLConnection.getResponseCode(); // 获得服务器的响应码
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream); // 处理服务器的响应结果
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return "";
	}

	/*
	 * Function : 封装请求体信息 Param : params请求体内容，encode编码格式
	 */
	public static StringBuffer getRequestData(Map<String, String> params, String encode) {
		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer
						.append(entry.getKey())
						.append("=")
						.append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(),
								encode)).append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	/*
	 * Function : 处理服务器的响应结果（将输入流转化成字符串） Param : inputStream服务器的响应输入流 Author :
	 */
	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null; // 存储处理结果
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toString());
		return resultData;
	}

	// Apache的post处理方式,现项目中因为传参为bean转换成的map,因此并未采用这种方式
	public static String doPost(String uriAPI, List<NameValuePair> params) {
		String result = "";
		HttpPost httpRequst = new HttpPost(uriAPI);// 创建HttpPost对象

		try {
			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (IOException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		}
		return result;
	}

	public static String doGet(String uriAPI) {
		String result = "";
		// HttpGet httpRequst = new HttpGet(URI uri);
		// HttpGet httpRequst = new HttpGet(String uri);
		// 创建HttpGet或HttpPost对象，将要请求的URL通过构造方法传入HttpGet或HttpPost对象。
		HttpGet httpRequst = new HttpGet(uriAPI);

		// new DefaultHttpClient().execute(HttpUriRequst requst);
		try {
			// 使用DefaultHttpClient类的execute方法发送HTTP GET请求，并返回HttpResponse对象。
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);// 其中HttpGet是HttpUriRequst的子类
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
				// 一般来说都要删除多余的字符
				result.replaceAll("\r", "");// 去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格
			} else {
				httpRequst.abort();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// result = e.getMessage().toString();
		} catch (IOException e) {
			e.printStackTrace();
			// result = e.getMessage().toString();
		}
		return result;
	}
}
