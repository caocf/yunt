package com.bepo.utils;

import android.text.Editable;
import android.text.TextWatcher;

class EditChangedListener implements TextWatcher {
	private CharSequence temp;// ����ǰ���ı�
	private int editStart;// ��꿪ʼλ��
	private int editEnd;// ������λ��
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
