package com.etong.android.frame.multiselectphotos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.etong.android.frame.R;
import com.etong.android.frame.multiselectphotos.photoview.PhotoView;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;
import com.etong.android.frame.widget.TitleBar;

//用于浏览图片
public class BrowserViewPagerActivity extends BaseSubscriberActivity implements
		CompoundButton.OnCheckedChangeListener {

	private PhotoView photoView;
	// 存放所有图片的路径
	private List<String> mPathList = new ArrayList<String>();
	private int mPosition;
	public static final int RESULT_CODE_SELECT_PICTURE = 8;
	public static final int RESULT_CODE_BROWSER_PICTURE = 13;

	private TitleBar mTitleBar;
	private ImgBrowserViewPager mViewPager;
	private TextView mTotalSizeTv;
	private CheckBox mPictureSelectedCb;

	/**
	 * 用来存储图片的选中情况
	 */
	private SparseBooleanArray mSelectMap = new SparseBooleanArray();

	@SuppressWarnings("deprecation")
	@Override
	protected void onInit(@Nullable Bundle savedInstanceState) {
		setContentView(R.layout.activity_image_browser);
		mTitleBar = new TitleBar(this);
		mTitleBar.setTitle("相册");

		mViewPager = (ImgBrowserViewPager) findViewById(R.id.img_browser_viewpager);
		mTotalSizeTv = (TextView) findViewById(R.id.total_size_tv);
		mPictureSelectedCb = (CheckBox) findViewById(R.id.picture_selected_cb);

		final Intent intent = this.getIntent();
		mPosition = intent.getIntExtra("position", 0);
		mPathList = intent.getStringArrayListExtra("pathList");

		PagerAdapter pagerAdapter = new PagerAdapter() {

			@Override
			public int getCount() {
				return mPathList.size();
			}

			/**
			 * 点击某张图片预览时，系统自动调用此方法加载这张图片左右视图（如果有的话）
			 */
			@Override
			public View instantiateItem(ViewGroup container, int position) {
				photoView = new PhotoView(container.getContext());
				photoView.setTag(position);
				String path = mPathList.get(position);
				if (path != null) {
					File file = new File(path);
					if (file.exists()) {
						Bitmap bitmap = BitmapLoader.getBitmapFromFile(path,
								mWidth, mHeight);
						if (bitmap != null) {
							photoView.setImageBitmap(bitmap);
						} else {
							photoView
									.setImageResource(R.drawable.picture_not_found);
						}
					} else {
						Bitmap bitmap = NativeImageLoader.getInstance()
								.getBitmapFromMemCache(path);
						if (bitmap != null) {
							photoView.setImageBitmap(bitmap);
						} else {
							photoView
									.setImageResource(R.drawable.picture_not_found);
						}
					}
				} else {
					photoView
							.setImageResource(R.drawable.picture_not_found);
				}
				container.addView(photoView, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				return photoView;
			}

			@Override
			public int getItemPosition(Object object) {
				View view = (View) object;
				int currentPage = mViewPager.getCurrentItem();
				if (currentPage == (Integer) view.getTag()) {
					return POSITION_NONE;
				} else {
					return POSITION_UNCHANGED;
				}
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView((View) object);
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

		};
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setOnPageChangeListener(onPageChangeListener);
		mPictureSelectedCb.setOnCheckedChangeListener(this);

		int[] pathArray = intent.getIntArrayExtra("pathArray");
		// 初始化选中了多少张图片
		for (int i = 0; i < pathArray.length; i++) {
			if (pathArray[i] == 1) {
				mSelectMap.put(i, true);
			}
		}
		mViewPager.setCurrentItem(mPosition);
		int currentItem = mViewPager.getCurrentItem();
		// 第一张特殊处理
		mPictureSelectedCb.setChecked(mSelectMap.get(currentItem));
		showSelected();
	}

	// 显示选中了多少张图片及选中的图片总的大小
	private void showSelected() {
		if (mSelectMap.size() > 0) {
			List<String> pathList = new ArrayList<String>();
			for (int i = 0; i < mSelectMap.size(); i++) {
				pathList.add(mPathList.get(mSelectMap.keyAt(i)));
			}
			String totalSize = BitmapLoader.getPictureSize(pathList);
			String totalText = "(" + mSelectMap.size() + "/"
					+ AlbumUtils.selectNum + ")"
					+ String.format("(%s)", totalSize);
			mTotalSizeTv.setText(totalText);
		} else {
			mTotalSizeTv.setText("");
		}
	}

	private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(final int i, float v, int i2) {

		}

		@Override
		public void onPageSelected(final int i) {
			mPictureSelectedCb.setChecked(mSelectMap.get(i));
			showSelected();
		}

		@Override
		public void onPageScrollStateChanged(int i) {

		}
	};

	/**
	 * 返回时将所选的图片路径(此处通过一个int数组记录所选的图片)返回PickPictureActivity,更新选中的图片
	 */
	@Override
	public void onBackPressed() {
		int pathArray[] = new int[mPathList.size()];
		for (int i = 0; i < pathArray.length; i++) {
			pathArray[i] = 0;
		}
		for (int j = 0; j < mSelectMap.size(); j++) {
			pathArray[mSelectMap.keyAt(j)] = 1;
		}
		mEventBus.post(pathArray, PickPictureActivity.TAG);
		finish();
	}

	@Override
	protected void back() {
		super.back();
		onBackPressed();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int currentItem = mViewPager.getCurrentItem();
		if (buttonView.getId() == R.id.picture_selected_cb) {
			if (mSelectMap.size() + 1 <= AlbumUtils.selectNum) {
				if (isChecked) {
					mSelectMap.put(currentItem, true);
				} else {
					mSelectMap.delete(currentItem);
				}
			} else if (isChecked) {
				toastMsg("你最多只能同时选择" + AlbumUtils.selectNum + "张图片");
				mPictureSelectedCb.setChecked(mSelectMap.get(currentItem));
			} else {
				mSelectMap.delete(currentItem);
			}

			showSelected();
		}
	}
}
