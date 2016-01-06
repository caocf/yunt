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
 * @���� ��������ģʽ,Ӧ������֮������ApplicationController�ĵ�������,���������е�mRequestQueue����������Ҫʱ����
 * @Ŀ�� Ϊ��������volley.jar��ʱ�� ���ظ������������ ,������õ���ģʽ����һ��mRequestQueue
 * 
 */

public class ApplicationController extends Application {

	/**
	 * Log or request
	 * 
	 * @���� TAG ��־�������ʾ
	 */
	public static final String TAG = "VolleyPatterns";

	/**
	 * Global request queue for Volley
	 * 
	 * @���� ȫ�ֵ�volley�������
	 */
	private RequestQueue mRequestQueue;

	/**
	 * A singleton instance of the application class for easy access in other
	 * places
	 * 
	 * @���� ���������������ṩ��һʵ��,�༶�ڲ���
	 * @���� �򵥵�˵���༶�ڲ���ָ���ǣ���static���εĳ�Աʽ�ڲ��ࡣ���û��static���εĳ�Աʽ�ڲ��౻��Ϊ�����ڲ��ࡣ
	 *     �༶�ڲ����൱�����ⲿ���static�ɷ֣����Ķ������ⲿ�����䲻����������ϵ����˿�ֱ�Ӵ������������ڲ����ʵ����
	 *     �ǰ����ⲿ����ʵ���е� �༶�ڲ����У����Զ��徲̬�ķ������ھ�̬������ֻ�ܹ������ⲿ���еľ�̬��Ա�������߳�Ա������
	 *     �����༶�ڲ����൱�����ⲿ��ĳ�Ա��ֻ���ڵ�һ�α�ʹ�õ�ʱ��ű���װ�ء�
	 */
	private static ApplicationController sInstance;
	IWXAPI msgApi;
	private Context context = this;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize the singleton
		// װ�䵥������
		sInstance = this;

		// �ѹ�Ӣ carsh �ϴ�
		PgyCrashManager.register(this);
		
		// ��ʼ��ͼƬ���ع���
		initImageUtils();

		// ��ʼ�� JPush
		JPushInterface.init(this);

		// ע��΢��֧��(�̻�APP����������΢��JAR��������APIǰ����Ҫ����΢��ע������APPID���������£�
		// API����ǰ����Ҫ����΢��ע������APP)
		msgApi = WXAPIFactory.createWXAPI(this, null);
		msgApi.registerApp(PayID.APP_ID);

		// okhttp ��ʼ��
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
				// �������ʱ���URI������MD5 ����
				.tasksProcessingOrder(QueueProcessingType.LIFO).discCacheFileCount(100)
				// ������ļ�����
				.discCache(new UnlimitedDiscCache(cacheDir))
				// �Զ��建��·��
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);// ȫ�ֳ�ʼ��������
	}

	public static synchronized ApplicationController getInstance() {
		return sInstance;
	}

	public void sendPayReq(PayReq req) {
		msgApi.sendReq(req);

	}

	// ============================����Ϊ volley ������==============================

	/**
	 * @���� ����Ҫʱ����volley���������mRequestQueue
	 */
	public RequestQueue getRequestQueue() {
		// ����ģʽװ���������,����һ������ʱ���е�ʵ����������
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mRequestQueue;
	}

	// ����ʾ����Ӷ�������
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		VolleyLog.d("Adding request to queue: %s", req.getUrl());
		getRequestQueue().add(req);
	}

	// ������ʾ����Ӷ�������
	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	// �Ӷ�����ȡ������
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}