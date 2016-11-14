package com.etong.android.frame.multiselectphotos;

import java.util.ArrayList;
import java.util.List;

import org.simple.eventbus.Subscriber;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.etong.android.frame.R;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;
import com.etong.android.frame.widget.TitleBar;

public class PickPictureActivity extends BaseSubscriberActivity implements
		OnClickListener, OnItemClickListener {
	public static final String TAG = "PickPictureActivity";

	// 此相册下所有图片的路径集合
	private List<String> mList;
	// 选中图片的路径集合
	private List<String> mPickedList;

	private PickPictureAdapter mAdapter;

	private static TitleBar mTitleBar;
	private GridView mGridView;

	public static final int RESULT_CODE_SELECT_PICTURE = 8;
	public static final int RESULT_CODE_BROWSER_PICTURE = 13;

	@Override
	protected void onInit(@Nullable Bundle savedInstanceState) {
		setContentView(R.layout.activity_pick_picture_detail);
		mTitleBar = new TitleBar(this);
		mTitleBar.setTitle("相册");
		mTitleBar.showBackButton(true);
		mTitleBar.showNextButton(false);

		Intent intent = this.getIntent();
		mGridView = (GridView) findViewById(R.id.child_grid);

		addClickListener(mTitleBar.getNextButton());

		mGridView.setOnItemClickListener(this);

		mList = intent.getStringArrayListExtra("data");
		mAdapter = new PickPictureAdapter(this, mList, mGridView, mDensity);
		mGridView.setAdapter(mAdapter);
	}

	@Subscriber(tag=TAG)
	public void onSelectResult(int[] selected) {
		int sum = 0;
		for (int i : selected) {
			if (i > 0) {
				++sum;
			}
		}
		if (sum > 0) {
			String sendText = "确定" + "(" + sum + "/" + AlbumUtils.selectNum+")";
			setNextButton(sendText);
		} else {
			setNextButton(null);
		}
		mAdapter.refresh(selected);

	}

	@Override
	public void onClick(View v) {
		// 点击确定按钮，返回选中的图片
		if (v.getId() == R.id.titlebar_next_button) {
			// 存放选中图片的路径
			mPickedList = new ArrayList<String>();
			// 存放选中的图片的position
			List<Integer> positionList;
			positionList = mAdapter.getSelectItems();
			// 拿到选中图片的路径
			for (int i = 0; i < positionList.size(); i++) {
				mPickedList.add(mList.get(positionList.get(i)));
			}
			if (mPickedList.size() < 1) {
				return;
			} else {
				// 返回选择的图片列表
				mEventBus.post(mPickedList, AlbumUtils.TAG);
				finish();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putStringArrayListExtra("pathList", (ArrayList<String>) mList);
		intent.putExtra("position", position);
		intent.putExtra("pathArray", mAdapter.getSelectedArray());
		intent.setClass(PickPictureActivity.this,
				BrowserViewPagerActivity.class);
		startActivity(intent);
	}

	public void setNextButton(String str) {
		if (TextUtils.isEmpty(str)) {
			mTitleBar.showNextButton(false);
		} else {
			mTitleBar.setNextButton(str);
		}
	}
}
