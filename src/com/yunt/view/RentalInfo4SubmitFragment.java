package com.yunt.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.MenJinAdapter;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;
import com.bepo.photo.AlbumActivity;
import com.bepo.photo.Bimp;
import com.bepo.photo.FileUtils;
import com.bepo.photo.GalleryActivity;
import com.bepo.photo.ImageItem;
import com.bepo.photo.PhotoGridAdapter;
import com.bepo.utils.CameraUtil;
import com.bepo.utils.MyTextUtils;
import com.bepo.view.SelectPicPop;
import com.kyleduo.switchbutton.SwitchButton;

public class RentalInfo4SubmitFragment extends Fragment {

	View view;
	Context context;

	// 车库有无门禁
	SwitchButton sb_HasParkControl;
	public static String HasParkControl;

	// 小区门禁类型
	GridView gridview;
	MenJinAdapter menJinAdapter;
	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	public static String CodeControlType;

	// 照片
	private Uri mImageCaptureUri;
	public static Bitmap bimap;
	private SelectPicPop mSelectPicPop;
	PhotoGridAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		view = inflater.inflate(R.layout.submit_rentalinfo, container, false);
		gridview = (GridView) view.findViewById(R.id.gridview);
		initData();
		initView();
		Bimp.tempSelectBitmap.clear();
		bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
		return view;
	}

	private void initView() {

		// 车库门禁
		HasParkControl = "0";
		sb_HasParkControl = (SwitchButton) view.findViewById(R.id.sb_HasParkControl);
		sb_HasParkControl.setChecked(false);
		sb_HasParkControl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					HasParkControl = "1";
				} else {
					HasParkControl = "0";
				}
			}
		});

		// 小区门禁类型
		CodeControlType = "1877";

		// 照片
		GridView noScrollgridview = (GridView) view.findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new PhotoGridAdapter(context);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (position == Bimp.tempSelectBitmap.size()) {
					mSelectPicPop = new SelectPicPop(getActivity(), itemsOnClick);
					mSelectPicPop.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					Intent intent = new Intent(context, GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", position);
					startActivityForResult(intent, 3);
				}
			}
		});

	}

	private void initData() {

		String url = PathConfig.ADDRESS + "/sys/sysdicvalue/queryDicvalue?name=CODE_CONTROL_TYPE";
		url = MyTextUtils.urlPlusAndFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				String jsondata = response.toString();
				data.clear();
				data = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				for (int i = 0; i < data.size(); i++) {
					data.get(i).put("flag", "0");
				}

				menJinAdapter = new MenJinAdapter(data, context);
				gridview.setAdapter(menJinAdapter);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	// 从相册选择 还是 照相的 选择 pop
	private OnClickListener itemsOnClick = new OnClickListener() {
		public void onClick(View v) {
			mSelectPicPop.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// File sd = Environment.getExternalStorageDirectory();
				// String path = sd.getPath() + "/GSM";
				// File file = new File(path);
				// if (!file.exists()) {
				// file.mkdir();
				// }
				// File f = new File(path, "temp.jpg");
				// if (f.exists()) {
				// f.delete();
				// }
				// mImageCaptureUri = Uri.fromFile(f);
				// intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				// mImageCaptureUri);
				// try {
				// startActivityForResult(intent, PathConfig.CAMERA);
				// } catch (ActivityNotFoundException e) {
				// System.out.println("照相出错");
				// }
				photo();
				break;

			case R.id.btn_pick_photo:

				Intent intent2 = new Intent(context, AlbumActivity.class);
				startActivityForResult(intent2, PathConfig.SELECT);

				// Intent intent2 = new Intent(Intent.ACTION_PICK, null);
				// intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				// IMAGE_UNSPECIFIED);
				// startActivityForResult(intent2, PathConfig.SELECT);
				break;

			default:
				break;
			}

		}

	};

	private static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File sd = Environment.getExternalStorageDirectory();
		String path = sd.getPath() + "/YUNT";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		File f = new File(path, "temp.jpg");
		if (f.exists()) {
			f.delete();
		}
		mImageCaptureUri = Uri.fromFile(f);
		openCameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		try {
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {
			System.out.println("照相出错");
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 3) {

				String fileName = String.valueOf(System.currentTimeMillis());
				Bitmap myBitmap = CameraUtil.compressImageFromFile(Environment.getExternalStorageDirectory()
						+ "/YUNT/temp.jpg");
				FileUtils.saveBitmap(myBitmap, fileName);

				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(myBitmap);
				Bimp.tempSelectBitmap.add(takePhoto);
				adapter.update();
			}
			break;
		case PathConfig.SELECT:
			adapter.update();
			break;
		case 3:
			adapter.update();
			break;
		}
	}

	//
	// // 选择图片 pop 对应的回调
	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// // 打开 Android 程序间共享
	// ContentResolver resolver = getActivity().getContentResolver();
	//
	// // 通过 相机 获得
	// if (requestCode == PathConfig.CAMERA) {
	// if (linPhoto.getChildCount() >= 4) {
	// ivPhoto.setVisibility(View.GONE);
	// ToastUtils.showSuperToastAlert(getActivity(), "只能添加四张图片");
	// } else {
	// try {
	//
	// myBitmap =
	// CameraUtil.compressImageFromFile(Environment.getExternalStorageDirectory()
	// + "/GSM/temp.jpg");
	// myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
	// flag = true;
	//
	// // 生成图像放入容器
	// final ImageView iv = new ImageView(getActivity());
	// LinearLayout.LayoutParams layoutParams = new
	// LinearLayout.LayoutParams(180, 180);
	// layoutParams.setMargins(10, 5, 10, 5);
	// iv.setLayoutParams(layoutParams);
	// iv.setScaleType(ScaleType.CENTER_CROP);
	// iv.setImageBitmap(myBitmap);
	// linPhoto.addView(iv);
	//
	// // 生成二进制放入list
	// String sss = CameraUtil.byte2hex(myByte);
	// picList.add(sss);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// // 通过 相册 获得
	// } else if (requestCode == PathConfig.SELECT) {
	// // myBitmap =
	// //
	// CameraUtil.compressImageFromFile(Environment.getExternalStorageDirectory()
	// // + "/GSM/temp.jpg");
	// try {
	//
	// if (data != null) {
	// Uri uri = data.getData();
	// myByte = CameraUtil.readStream(resolver.openInputStream(uri));
	// myBitmap = CameraUtil.getPicFromBytes(myByte);
	// myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
	// flag = true;
	// myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
	// long len = myByte.length;
	// flag = true;
	//
	// // 生成图像放入容器
	// final ImageView iv = new ImageView(getActivity());
	// LinearLayout.LayoutParams layoutParams = new
	// LinearLayout.LayoutParams(180, 180);
	// layoutParams.setMargins(10, 5, 10, 5);
	// iv.setLayoutParams(layoutParams);
	// iv.setScaleType(ScaleType.CENTER_CROP);
	// iv.setImageBitmap(myBitmap);
	// linPhoto.addView(iv);
	//
	// // 生成二进制放入list
	// String sss = CameraUtil.byte2hex(myByte);
	// picList.add(sss);
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// }
}
