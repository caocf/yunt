package com.bepo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bepo.R;
import com.yunt.view.RentalTime4SubmitFragment;

@SuppressWarnings("rawtypes")
public class WeekAdapter extends CustomAdapter {

	private Context context;
	private List<HashMap<String, String>> data;
	private LayoutInflater inflater;
	private List<Boolean> isClicked = new ArrayList<Boolean>(); // 定义一个向量作为选中与否容器

	@SuppressWarnings("unchecked")
	public WeekAdapter(ArrayList<HashMap<String, String>> data, Context context) {
		super(data, context);
		this.context = context;
		this.data = (ArrayList<HashMap<String, String>>) data;
		inflater = ((Activity) context).getLayoutInflater();

		isClicked.add(0, true);
		isClicked.add(1, true);
		isClicked.add(2, true);
		isClicked.add(3, true);
		isClicked.add(4, true);
		isClicked.add(5, false);
		isClicked.add(6, false);

		for (int i = 1; i < 6; i++) {
			RentalTime4SubmitFragment.week.add(i + "");
		}

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Activity activity = (Activity) context;
		ViewHolder viewCache;
		// if (convertView == null) {
		// convertView = inflater.inflate(R.layout.menjin_tem, parent,
		// false);
		convertView = LayoutInflater.from(context).inflate(R.layout.menjin_tem, null);
		viewCache = new ViewHolder();

		viewCache.text = (TextView) convertView.findViewById(R.id.text);
		viewCache.linText = (LinearLayout) convertView.findViewById(R.id.linText);

		// ToastUtils.showSuperToastAlert(activity,
		// data.get(position).get("flag") +
		// data.get(position).get("NAME_C"));

		if (isClicked.get(position)) {
			viewCache.linText.setBackground(activity.getResources().getDrawable(R.drawable.sharp));
			viewCache.text.setTextColor(activity.getResources().getColor(R.color.white));
		} else {
			viewCache.linText.setBackground(activity.getResources().getDrawable(R.drawable.mejin_bg));
			viewCache.text.setTextColor(activity.getResources().getColor(R.color.gray_user_bg));
		}
		convertView.setTag(viewCache);
		// }

		viewCache.text.setText(data.get(position).get("NAME_C"));

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Boolean temp = isClicked.get(position);
				isClicked.set(position, !temp);
				notifyDataSetChanged();

				RentalTime4SubmitFragment.week.clear();
				for (int i = 0; i < isClicked.size(); i++) {
					if (isClicked.get(i)) {
						if (i == 6) {
							RentalTime4SubmitFragment.week.add("0");
						} else {
							int sss = i + 1;
							RentalTime4SubmitFragment.week.add(sss + "");
						}
					}
				}

			}
		});
		return convertView;
	}

	@Override
	public int getCount() {
		return data.size();

	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		public LinearLayout linText;
		public TextView text;

	}

}
