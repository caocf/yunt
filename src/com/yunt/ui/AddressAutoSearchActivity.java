package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

//时间:2015年10月14日15:04:47
//遗留问题:区域编码和城市编码对搜索造成的影响 

public class AddressAutoSearchActivity extends BaseAct implements TextWatcher, OnClickListener,
		OnGeocodeSearchListener {

	private GeocodeSearch geocoderSearch;

	private LinearLayout linBack;
	private AutoCompleteTextView searchText;
	private ListView addressListView;
	private RelativeLayout rlCity;
	public static TextView tvCity;
	private ImageView ivClose;

	private String city;
	private String defaultAddress;
	private LatLng mLatLng;
	public static SimpleAdapter adapter;
	public static ArrayList<HashMap<String, String>> data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adress_auto_search);

		defaultAddress = getIntent().getExtras().getString("defaultAddress");
		city = getIntent().getExtras().getString("city");
		city = city.replace("市", "");
		initView();
		// initListViewData(defaultAddress);

	}

	private void initListViewData(String defaultAddress2) {
		showDialog();
		String newText = defaultAddress2;
		Inputtips inputTips = new Inputtips(AddressAutoSearchActivity.this, new InputtipsListener() {
			@Override
			public void onGetInputtips(List<Tip> tipList, int rCode) {

				if (rCode == 0) {
					data = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < tipList.size(); i++) {
						HashMap<String, String> item = new HashMap<String, String>();
						item.put("address", tipList.get(i).getName());
						item.put("miaoshu", tipList.get(i).getDistrict());
						data.add(item);
					}

					adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.chose_address_list_item,
							new String[] { "address", "miaoshu" }, new int[] { R.id.tvaddress, R.id.tvMiaoshu });
					addressListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					addressListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							String keyword = data.get(arg2).get("address");
							getLatlon(keyword);
						}

					});
				}
			}
		});

		try {
			inputTips.requestInputtips(newText, city);
			// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
		} catch (AMapException e) {
			e.printStackTrace();
		}
		dismissDialog();
	}

	private void initView() {

		addressListView = (ListView) this.findViewById(R.id.addressListView);

		linBack = (LinearLayout) this.findViewById(R.id.linBack);
		linBack.setOnClickListener(this);

		ivClose = (ImageView) this.findViewById(R.id.ivClose);
		ivClose.setVisibility(View.GONE);
		ivClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				searchText.setText("");
			}
		});

		rlCity = (RelativeLayout) this.findViewById(R.id.rlCity);
		rlCity.setOnClickListener(this);

		tvCity = (TextView) this.findViewById(R.id.tvCity);
		tvCity.setText(city);

		searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
		searchText.addTextChangedListener(this);

		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void afterTextChanged(Editable arg0) {

		showDialog();
		String newText = arg0.toString().trim();
		Inputtips inputTips = new Inputtips(AddressAutoSearchActivity.this, new InputtipsListener() {
			@Override
			public void onGetInputtips(List<Tip> tipList, int rCode) {

				if (rCode == 0) {
					data = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < tipList.size(); i++) {
						HashMap<String, String> item = new HashMap<String, String>();
						item.put("address", tipList.get(i).getName());
						item.put("miaoshu", tipList.get(i).getDistrict());
						data.add(item);
					}

					adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.chose_address_list_item,
							new String[] { "address", "miaoshu" }, new int[] { R.id.tvaddress, R.id.tvMiaoshu });
					addressListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					addressListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							String keyword = data.get(arg2).get("address");
							getLatlon(keyword);
						}

					});
				}
			}
		});

		try {
			inputTips.requestInputtips(newText, city);// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
		} catch (AMapException e) {
			e.printStackTrace();
		}
		dismissDialog();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (MyTextUtils.isEmpty(s.toString())) {
			ivClose.setVisibility(View.GONE);
		} else {
			ivClose.setVisibility(View.VISIBLE);
		}
	}

	// 根据位置解析出经纬度
	public void getLatlon(final String name) {
		GeocodeQuery query = new GeocodeQuery(name, "");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.linBack:
			finish();
			break;
		case R.id.rlCity:
			Intent intent = new Intent(AddressAutoSearchActivity.this, CityListAct.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	public static void setCity(String text) {
		tvCity.setText(text);
	}

	// 通过地址解析经纬度的回调
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				mLatLng = new LatLng(address.getLatLonPoint().getLatitude(), address.getLatLonPoint()
						.getLongitude());
				HomeAct.moveCar(mLatLng);
				finish();
			}
		}
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {

	}

}
