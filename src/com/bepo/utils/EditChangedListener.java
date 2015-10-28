package com.bepo.utils;

import android.text.Editable;
import android.text.TextWatcher;

class EditChangedListener implements TextWatcher {
	private CharSequence temp;// 监听前的文本
	private int editStart;// 光标开始位置
	private int editEnd;// 光标结束位置
	private final int charMaxNum = 10;

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		temp = s;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		

	}
};
