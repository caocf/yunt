package com.bepo.core;

import java.util.ArrayList;

public class PathConfig {

	public static final int CAMERA = 0001;
	public static final int SELECT = 0002;
	public static final int LOCATION = 0003;
	public static final int event_status_close = 0004;// �¼�ע����ʾ
	public static final int event_status_todo = 0005;// �¼������ʾ

	// web������·��

	// public static final String ADDRESS = "http://192.168.2.77:8080/";//
	// public static final String ADDRESS = "http://192.168.2.177:8080/";
	// public static final String ADDRESS = "http://121.22.104.218:8888";//
	// �����ӷ�����

	public static final String ADDRESS = "http://www.sharecar.cn";
	// public static final String ADDRESS = "http://192.168.1.105:8080/zcw";
	// public static final String ADDRESS = "http://192.168.1.107:8080/zcw";

	public static final String UDP_HOST = "121.22.104.218";// ��λ��IP
	public static final int UDP_PORT = 8603;// ��λ���˿�
	public static String ukey = "";// ��½�ɹ��󣬻���������ukey���൱�����ʶ����
	public static String clientkey = "";
	public static String userCode = "";
	public static String girdCode = "";
	public static String userPhone = "";

	public static String appraiseJson = "";// �����
	public static String fromJson = "";// �¼���Դ
	public static String typeJson = "";// �¼�����
	public static String statusJson = "";// �¼�״̬
	public static String assignmentUnit = "";// ָ�ɵ�λ
	public static ArrayList<String> xdata = new ArrayList<String>();
	public static ArrayList<String> ydata = new ArrayList<String>();
	public static String eventSearchUrl = "";
	public static boolean isRuning = false;

}
