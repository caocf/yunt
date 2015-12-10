package com.bepo.core;

import java.util.ArrayList;

public class PathConfig {

	public static final int CAMERA = 0001;
	public static final int SELECT = 0002;
	public static final int LOCATION = 0003;
	public static final int event_status_close = 0004;// 事件注销标示
	public static final int event_status_todo = 0005;// 事件待办标示

	// web服务器路径

	// public static final String ADDRESS = "http://192.168.2.77:8080/";//
	// public static final String ADDRESS = "http://192.168.2.177:8080/";
	// public static final String ADDRESS = "http://121.22.104.218:8888";//
	// 北戴河服务器

	public static final String ADDRESS = "http://www.sharecar.cn";
	// public static final String ADDRESS = "http://192.168.1.105:8080/zcw";
	// public static final String ADDRESS = "http://192.168.1.107:8080/zcw";

	public static final String UDP_HOST = "121.22.104.218";// 上位机IP
	public static final int UDP_PORT = 8603;// 上位机端口
	public static String ukey = "";// 登陆成功后，缓存下来的ukey，相当于身份识别码
	public static String clientkey = "";
	public static String userCode = "";
	public static String girdCode = "";
	public static String userPhone = "";

	public static String appraiseJson = "";// 满意度
	public static String fromJson = "";// 事件来源
	public static String typeJson = "";// 事件类型
	public static String statusJson = "";// 事件状态
	public static String assignmentUnit = "";// 指派单位
	public static ArrayList<String> xdata = new ArrayList<String>();
	public static ArrayList<String> ydata = new ArrayList<String>();
	public static String eventSearchUrl = "";
	public static boolean isRuning = false;

}
