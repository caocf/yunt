package com.bepo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class PicUtils {

	private static final String BOUNDARY = "----WebKitFormBoundaryT1HoybnYeFOGFlBR";

	/**
	 * 
	 * @param params
	 *            ���ݵ���ͨ����
	 * @param uploadFile
	 *            ��Ҫ�ϴ����ļ���
	 * @param fileFormName
	 *            ��Ҫ�ϴ��ļ����е�����
	 * @param newFileName
	 *            �ϴ����ļ����ƣ�����д��ΪuploadFile������
	 * @param urlStr
	 *            �ϴ��ķ�������·��
	 * @throws IOException
	 */
	public void uploadForm(Map<String, String> params, String fileFormName, File uploadFile, String newFileName,
			String urlStr) throws IOException {
		if (newFileName == null || newFileName.trim().equals("")) {
			newFileName = uploadFile.getName();
		}

		StringBuilder sb = new StringBuilder();
		/**
		 * ��ͨ�ı�����
		 */
		if (params != null)
			for (String key : params.keySet()) {
				sb.append("--" + BOUNDARY + "\r\n");
				sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n");
				sb.append("\r\n");
				sb.append(params.get(key) + "\r\n");
			}
		/**
		 * �ϴ��ļ���ͷ
		 */
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"" + fileFormName + "\"; filename=\"" + newFileName + "\""
				+ "\r\n");
		sb.append("Content-Type: image/jpeg" + "\r\n");// ��������������ļ����͵�У�飬������ȷָ��ContentType
		sb.append("\r\n");

		byte[] headerInfo = sb.toString().getBytes("UTF-8");
		byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
		System.out.println(sb.toString());
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		conn.setRequestProperty("Content-Length",
				String.valueOf(headerInfo.length + uploadFile.length() + endInfo.length));
		conn.setDoOutput(true);

		OutputStream out = conn.getOutputStream();
		InputStream in = new FileInputStream(uploadFile);
		out.write(headerInfo);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) != -1)
			out.write(buf, 0, len);

		out.write(endInfo);
		in.close();
		out.close();
		if (conn.getResponseCode() == 200) {
			System.out.println("�ϴ��ɹ�");
		}

	}

	/**
	 * 
	 * @param params
	 *            ���ݵ���ͨ����
	 * @param uploadFile
	 *            ��Ҫ�ϴ����ļ���
	 * @param fileFormName
	 *            ��Ҫ�ϴ��ļ����е�����
	 * @param newFileName
	 *            �ϴ����ļ����ƣ�����д��ΪuploadFile������
	 * @param urlStr
	 *            �ϴ��ķ�������·��
	 * @throws IOException
	 */
	public void uploadFromBySocket(Map<String, String> params, String fileFormName, File uploadFile,
			String newFileName, String urlStr) throws IOException {
		if (newFileName == null || newFileName.trim().equals("")) {
			newFileName = uploadFile.getName();
		}

		StringBuilder sb = new StringBuilder();
		/**
		 * ��ͨ�ı�����
		 */
		if (params != null)
			for (String key : params.keySet()) {
				sb.append("--" + BOUNDARY + "\r\n");
				sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n");
				sb.append("\r\n");
				sb.append(params.get(key) + "\r\n");
			}
		else {
			sb.append("\r\n");
		}

		/**
		 * �ϴ��ļ���ͷ
		 */
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"" + fileFormName + "\"; filename=\"" + newFileName + "\""
				+ "\r\n");
		sb.append("Content-Type: image/jpeg" + "\r\n");// ��������������ļ����͵�У�飬������ȷָ��ContentType
		sb.append("\r\n");

		byte[] headerInfo = sb.toString().getBytes("UTF-8");
		byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

		System.out.println(sb.toString());

		URL url = new URL(urlStr);
		Socket socket = new Socket(url.getHost(), url.getPort());
		OutputStream os = socket.getOutputStream();
		PrintStream ps = new PrintStream(os, true, "UTF-8");

		// д������ͷ
		ps.println("POST " + urlStr + " HTTP/1.1");
		ps.println("Content-Type: multipart/form-data; boundary=" + BOUNDARY);
		ps.println("Content-Length: " + String.valueOf(headerInfo.length + uploadFile.length() + endInfo.length));

		InputStream in = new FileInputStream(uploadFile);
		// д������
		os.write(headerInfo);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) != -1)
			os.write(buf, 0, len);

		os.write(endInfo);

		in.close();
		os.close();
		ps.close();
	}

	public static void main(String[] args) {
		try {

			File file = new File("D:/dtd", "dwr30.dtd");

			new PicUtils().uploadForm(null, "uploadFile", file, "helloworld.txt",
					"http://localhost:8080/strurts2fileupload/uploadAction");

			new PicUtils().uploadFromBySocket(null, "uploadFile", file, "hibernate-configuration-3.0.dtd",
					"http://localhost:8080/strurts2fileupload/uploadAction");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
