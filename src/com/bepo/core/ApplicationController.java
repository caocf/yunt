package com.bepo.core;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.bepo.pay.PayID;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.okhttp.OkHttpUtils;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.update.PgyUpdateManager;
import com.squareup.okhttp.OkHttpClient;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 
 * @author simas
 * @time 2015-1-22 10:00:50
 * @描述 饿汉单例模式,应用启动之初创建ApplicationController的单例对象,并将对象中的mRequestQueue的属性在需要时创建
 * @目的 为了在利用volley.jar的时候 不重复创建请求队列 ,因此利用单例模式创建一个mRequestQueue
 * 
 */

public class ApplicationController extends Application {

	/**
	 * Log or request
	 * 
	 * @描述 TAG 日志或请求标示
	 */
	public static final String TAG = "VolleyPatterns";

	/**
	 * Global request queue for Volley
	 * 
	 * @描述 全局的volley请求队列
	 */
	private RequestQueue mRequestQueue;

	/**
	 * A singleton instance of the application class for easy access in other
	 * places
	 * 
	 * @描述 给所有其他对象提供这一实例,类级内部类
	 * @解释 简单点说，类级内部类指的是，有static修饰的成员式内部类。如果没有static修饰的成员式内部类被称为对象级内部类。
	 *     类级内部类相当于其外部类的static成分，它的对象与外部类对象间不存在依赖关系，因此可直接创建。而对象级内部类的实例，
	 *     是绑定在外部对象实例中的 类级内部类中，可以定义静态的方法。在静态方法中只能够引用外部类中的静态成员方法或者成员变量。
	 *     　　类级内部类相当于其外部类的成员，只有在第一次被使用的时候才被会装载。
	 */
	private static ApplicationController sInstance;
	IWXAPI msgApi;
	private Context context = this;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		// 装配单例对象
		sInstance = this;

		// 蒲公英 carsh 上传
		PgyCrashManager.register(this);
		
		// 初始化图片加载工具
		initImageUtils();

		// 初始化 JPush
		JPushInterface.init(this);

		// 注册微信支付(商户APP工程中引入微信JAR包，调用API前，需要先向微信注册您的APPID，代码如下：
		// API调用前，需要先向微信注册您的APP)
		msgApi = WXAPIFactory.createWXAPI(this, null);
		msgApi.registerApp(PayID.APP_ID);

		// okhttp 初始化
		OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
		client.setConnectTimeout(200000, TimeUnit.MILLISECONDS);

	}

	@SuppressWarnings("deprecation")
	private void initImageUtils() {

		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageloader/Cache");

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800).threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024).discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO).discCacheFileCount(100)
				// 缓存的文件数量
				.discCache(new UnlimitedDiscCache(cacheDir))
				// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	public static synchronized ApplicationController getInstance() {
		return sInstance;
	}

	public void sendPayReq(PayReq req) {
		msgApi.sendReq(req);

	}

	// ============================以下为 volley 的配置==============================

	/**
	 * @描述 在需要时创建volley的请求队列mRequestQueue
	 */
	public RequestQueue getRequestQueue() {
		// 懒汉模式装配请求队列,当第一次请求时队列的实例将被创建
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}

	// 带标示的添加队列请求
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		VolleyLog.d("Adding request to queue: %s", req.getUrl());
		getRequestQueue().add(req);
	}

	// 不带标示的添加队列请求
	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	// 从队列中取消请求
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}