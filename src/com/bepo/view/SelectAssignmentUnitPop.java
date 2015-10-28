package com.bepo.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.SimpleTreeAdapter;
import com.bepo.bean.AllGridTreeBean;
import com.bepo.bean.FileBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;
import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeListViewAdapter;
import com.zhy.tree.bean.TreeListViewAdapter.OnTreeNodeClickListener;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author simas
 * @describe 指派单位pop
 * @time 2015-6-1 10:40:39
 * 
 */

public class SelectAssignmentUnitPop extends PopupWindow {

	private List<FileBean> mDatas = new ArrayList<FileBean>();
	public static ArrayList<Map<String, String>> metaData = new ArrayList<Map<String, String>>();// 元数据
	private ListView mTree;
	private TreeListViewAdapter mAdapter;
	private View View;
	Context mContext;

	@SuppressWarnings("deprecation")
	public SelectAssignmentUnitPop(final Activity context, OnClickListener itemsOnClick) {
		super(context);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable();
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View = inflater.inflate(R.layout.tree_fragment, null);
		mContext = context;
		initData();
		// initDatas();

		// 设置SelectPicPopupWindow的View
		this.setContentView(View);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		// this.setAnimationStyle(R.style.SelectEventFromStyle);
		this.setAnimationStyle(R.style.pop_roate);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		View.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = View.findViewById(R.id.tree_lin).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height || y > height) {
						dismiss();
					}
				}
				int width = View.findViewById(R.id.tree_lin).getLeft();
				int x = (int) event.getX();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (x < width || x > width) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

	void formatData() {
		mDatas.clear();
		for (int i = 0; i < metaData.size(); i++) {
			String code = metaData.get(i).get("CODE").toString();
			String pcode = metaData.get(i).get("PCODE").toString();
			String name = metaData.get(i).get("NAME").toString();
			mDatas.add(new FileBean(code, pcode, name));

		}
		mTree = (ListView) View.findViewById(R.id.id_tree);
		try {
			mAdapter = new SimpleTreeAdapter<FileBean>(mTree, mContext, mDatas, 1);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		mTree.setAdapter(mAdapter);
		mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
			@Override
			public void onClick(Node node, int position) {
				if (node.isLeaf()) {

					AllGridTreeBean mAllGridTreeBean = new AllGridTreeBean();
					mAllGridTreeBean.setCODE(node.getId());
					mAllGridTreeBean.setNAME(node.getName());
					EventBus.getDefault().post(mAllGridTreeBean);
					dismiss();
				}
			}

		});
	}

	private void initData() {

		if (PathConfig.assignmentUnit.equals("")) {
			String url = PathConfig.ADDRESS + "gsm/base/bseatgird/queryAllGridTree?ukey=" + PathConfig.ukey;
			StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					String jsondata = response.toString();
					PathConfig.assignmentUnit = jsondata;
					metaData = JSON.parseObject(jsondata, new TypeReference<ArrayList<Map<String, String>>>() {
					});
					formatData();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e("TAG", error.getMessage(), error);
				}
			});
			ApplicationController.getInstance().addToRequestQueue(stringRequest);
		} else {
			metaData.clear();
			metaData = JSON.parseObject(PathConfig.assignmentUnit,
					new TypeReference<ArrayList<Map<String, String>>>() {
					});

			formatData();
		}

	}
}
