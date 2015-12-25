/**  
 * Project Name:Android_Car_Example  
 * File Name:InputTipTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015��4��7������10:42:41  
 *  
 */

package com.yunt.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;

public class InputTipTask implements InputtipsListener {

	private static InputTipTask mInputTipTask;

	private Inputtips mInputTips;

	private RecomandAdapter mAdapter;

	public static InputTipTask getInstance(Context context, RecomandAdapter adapter) {
		if (mInputTipTask == null) {
			mInputTipTask = new InputTipTask(context);
		}
		// �����������ν���DestinationActivity��������RecomandAdapter����᲻��ͬһ��
		mInputTipTask.setRecommandAdapter(adapter);
		return mInputTipTask;
	}

	public void setRecommandAdapter(RecomandAdapter adapter) {
		mAdapter = adapter;
	}

	private InputTipTask(Context context) {
		mInputTips = new Inputtips(context, this);

	}

	public void searchTips(String keyWord, String city) {
		try {
			mInputTips.requestInputtips(keyWord, city);
		} catch (AMapException e) {
			e.printStackTrace();

		}
	}

	@Override
	public void onGetInputtips(List<Tip> tips, int resultCode) {

		if (resultCode == 0 && tips != null) {
			ArrayList<PositionEntity> positions = new ArrayList<PositionEntity>();
			for (Tip tip : tips) {
				positions.add(new PositionEntity(0, 0, tip.getName(), "", "", tip.getAdcode(), ""));
			}
			mAdapter.setPositionEntities(positions);
			mAdapter.notifyDataSetChanged();
		}
		// TODO ���Ը���app��������Բ�ѯ�������������Ӧ����ʾ�����߼�����
	}

}
