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
	 * javaԭ���ṩ��post��ʽ Function : ����Post���󵽷����� Param : params���������ݣ�encode�����ʽ
	 */
	public static String submitPostData(Map<String, String> params, String urlStr) {

		byte[] data = getRequestData(params, "utf-8").toString().getBytes();// ���������
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(3000); // �������ӳ�ʱʱ��
			httpURLConnection.setDoInput(true); // �����������Ա�ӷ�������ȡ����
			httpURLConnection.setDoOutput(true); // ����������Ա���������ύ����
			httpURLConnection.setRequestMethod("POST"); // ������Post��ʽ�ύ����
			httpURLConnection.setUseCaches(false); // ʹ��Post��ʽ����ʹ�û���
			// ������������������ı�����
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// ����������ĳ���
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
			// �����������������д������
			// �˴�getOutputStream�������Ľ���connect(������ͬ���������connect()������
			// �����ڿ����в�����������connect()Ҳ����)��
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);

			int response = httpURLConnection.getResponseCode(); // ��÷���������Ӧ��
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream); // �������������Ӧ���
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return "";
	}

	/*
	 * Function : ��װ��������Ϣ Param : params���������ݣ�encode�����ʽ
	 */
	public static StringBuffer getRequestData(Map<String, String> params, String encode) {
		StringBuffer stringBuffer = new StringBuffer(); // �洢��װ�õ���������Ϣ
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer
						.append(entry.getKey())
						.append("=")
						.append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(),
								encode)).append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // ɾ������һ��"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	/*
	 * Function : �������������Ӧ�������������ת�����ַ����� Param : inputStream����������Ӧ������ Author :
	 */
	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null; // �洢������
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

	// Apache��post����ʽ,����Ŀ����Ϊ����Ϊbeanת���ɵ�map,��˲�δ�������ַ�ʽ
	public static String doPost(String uriAPI, List<NameValuePair> params) {
		String result = "";
		HttpPost httpRequst = new HttpPost(uriAPI);// ����HttpPost����

		try {
			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// ȡ��Ӧ���ַ���
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
		// ����HttpGet��HttpPost���󣬽�Ҫ�����URLͨ�����췽������HttpGet��HttpPost����
		HttpGet httpRequst = new HttpGet(uriAPI);

		// new DefaultHttpClient().execute(HttpUriRequst requst);
		try {
			// ʹ��DefaultHttpClient���execute��������HTTP GET���󣬲�����HttpResponse����
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);// ����HttpGet��HttpUriRequst������
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// ȡ��Ӧ���ַ���
				// һ����˵��Ҫɾ��������ַ�
				result.replaceAll("\r", "");// ȥ�����ؽ���е�"\r"�ַ���������ڽ���ַ���������ʾһ��С����
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
