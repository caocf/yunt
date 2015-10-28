package com.bepo.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.ui.LoginAct;
import com.yunt.ui.LoginActivity;
import com.yunt.ui.MainActivity;

public class UpdateManager {
	private static final int DOWN = 1;// ����������������
	private static final int DOWN_FINISH = 0;// ���������������
	private static final int CHECK_UPDATE = 2;// ���������������
	private HashMap<String, String> hashMap;// �洢���°汾��xml��Ϣ
	private String fileSavePath;// ������apk�Ĵ���λ��
	private String updateVersionXMLPath;// �����µ�xml�ļ�
	private int progress;// ��ȡ��apk������������,�������ع�����
	private boolean cancelUpdate = false;// �Ƿ�ȡ������
	private Context context;
	private ProgressBar progressBar;
	private Dialog downLoadDialog;
	// ����UI
	private Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch ((Integer) msg.obj) {
			case CHECK_UPDATE:
				// ��ʾ��ʾ�Ի���
				showUpdateVersionDialog();
				break;
			case DOWN:
				progressBar.setProgress(progress);
				break;
			case DOWN_FINISH:
				Toast.makeText(context, "�ļ��������,���ڰ�װ����", Toast.LENGTH_SHORT).show();
				installAPK();
				break;

			default:
				break;
			}
		}

	};

	public void stop() {

	}

	/**
	 * ���췽��
	 * 
	 * @param updateVersionXMLPath
	 *            �Ƚϰ汾��xml�ļ���ַ(�������ϵ�)
	 * @param context
	 *            ������
	 */
	public UpdateManager(String updateVersionXMLPath, Context context) {
		super();
		this.updateVersionXMLPath = updateVersionXMLPath;
		this.context = context;
	}

	/**
	 * ����Ƿ�ɸ���
	 * 
	 * @return
	 */
	public void checkUpdate() {
		if (isUpdate()) {
			Message message = new Message();
			message.obj = CHECK_UPDATE;
			handler.sendMessage(message);
		} else {
			setIntent();
		}
	}

	/**
	 * ������ʾ��
	 */
	private void showUpdateVersionDialog() {
		// // ����Ի���
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("�������");
		builder.setMessage("������°汾�ˣ��Ƿ������");
		// ����
		builder.setPositiveButton("����", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// // ��ʾ���ضԻ���
				showDownloadDialog();
			}
		});
		// �Ժ����
		builder.setNegativeButton("�˳�", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				((BaseAct) context).finish();
				// ToastUtils.showSuperToastAlert(context, "����汾̫��,�޷����ӷ�����");
			}
		});
		// builder.setOnCancelListener(new OnCancelListener() {
		//
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// setIntent();
		// }
		// });
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
		downLoadDialog = builder.create();
		// ��Ӱ�ť֮��Ľ������
		downLoadDialog.setCanceledOnTouchOutside(false);
		downLoadDialog.setCancelable(false);
		downLoadDialog.show();
	}

	/**
	 * ���ص���ʾ��
	 */
	protected void showDownloadDialog() {
		{
			// ����������ضԻ���
			AlertDialog.Builder builder = new Builder(context);

			builder.setTitle("���ڸ���");
			// �����ضԻ������ӽ�����
			final LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.softupdate_progress, null);
			progressBar = (ProgressBar) v.findViewById(R.id.update_progress);
			builder.setView(v);
			// ȡ������
			builder.setNegativeButton("ȡ��", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// ����ȡ��״̬
					cancelUpdate = true;

				}
			});
			downLoadDialog = builder.create();
			// ��Ӱ�ť֮��Ľ������
			downLoadDialog.setCanceledOnTouchOutside(false);
			downLoadDialog.setCancelable(false);
			downLoadDialog.show();
			// �����ļ�
			downloadApk();
		}

	}

	/**
	 * ����apk,����ռ�����߳�.���������߳�
	 */
	private void downloadApk() {
		new downloadApkThread().start();

	}

	/**
	 * �ж��Ƿ�ɸ���
	 * 
	 * @return
	 */
	private boolean isUpdate() {
		int versionCode = getVersionCode(context);

		try {
			// ��ȡweb��������updateVersionXMLPath·���µ�version.XML
			URL url = new URL(updateVersionXMLPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(3 * 1000);
			conn.setRequestMethod("GET");// ����Ҫ��д
			conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			InputStream inputStream = conn.getInputStream();
			// ����XML�ļ�
			ParseXmlService service = new ParseXmlService();
			hashMap = service.parseXml(inputStream);

			if (hashMap != null) {
				int serverCode = Integer.valueOf(hashMap.get("version"));
				if (serverCode > versionCode) {
					System.out.println("�°汾�ǣ�========================= " + serverCode);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// �汾�ж�

		return false;

	}

	/**
	 * ��ȡ��ǰ�汾�ͷ������汾.����������汾���ڱ��ذ�װ�İ汾.�͸���
	 * 
	 * @param context2
	 * @return
	 */
	private int getVersionCode(Context context2) {
		int versionCode = 0;
		try {
			// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
			versionCode = context.getPackageManager().getPackageInfo("com.bepo", 0).versionCode;
			// System.out.println("��ǰ�汾�ǣ�========================= " +
			// versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;

	}

	/**
	 * ��װapk�ļ�
	 */
	private void installAPK() {
		File apkfile = new File(fileSavePath, hashMap.get("fileName") + ".apk");
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		System.out.println("filepath=" + apkfile.toString() + "  " + apkfile.getPath());
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		context.startActivity(i);
		// apkfile.delete();
		android.os.Process.killProcess(android.os.Process.myPid());// ������������Ļ���apk��װ���֮�������������
	}

	/**
	 * ж��Ӧ�ó���(û���õ�)
	 */
	public void uninstallAPK() {
		Uri packageURI = Uri.parse("package:com.bepo");
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		context.startActivity(uninstallIntent);

	}

	/**
	 * ����apk�ķ���
	 * 
	 */
	public class downloadApkThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					fileSavePath = sdpath + "download";
					URL url = new URL(hashMap.get("url"));
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(5 * 1000);// ���ó�ʱʱ��
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Charser", "GBK,utf-8;q=0.7,*;q=0.3");
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(fileSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(fileSavePath, hashMap.get("fileName") + ".apk");
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						Message message = new Message();
						message.obj = DOWN;
						handler.sendMessage(message);
						if (numread <= 0) {
							// �������
							// ȡ�����ضԻ�����ʾ
							downLoadDialog.dismiss();
							Message message2 = new Message();
							message2.obj = DOWN_FINISH;
							handler.sendMessage(message2);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setIntent() {

		Intent mIntent = new Intent(context, LoginActivity.class);
//		Intent mIntent = new Intent(context, MainActivity.class);
		context.startActivity(mIntent);
		((BaseAct) context).finish();
	}

}
