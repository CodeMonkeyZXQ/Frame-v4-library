package com.etong.android.frame.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class ListAdapter<T> extends ArrayAdapter<T> {
	private Context mListContext;
	private int mListResource;

	public ListAdapter(Context context, int resource) {
		super(context, resource);
		mListResource = resource;
		mListContext = context;
	}

	private View inflateView(View view) {
		if (null == view) {
			LayoutInflater inflater = LayoutInflater.from(mListContext);
			view = inflater.inflate(mListResource, null);
		}
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflateView(convertView);
		onPaint(view, getItem(position), position);
		return view;
	}

	abstract protected void onPaint(View view, T data, int position);
}