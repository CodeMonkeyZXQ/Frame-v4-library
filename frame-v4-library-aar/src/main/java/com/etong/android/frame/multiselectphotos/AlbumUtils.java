package com.etong.android.frame.multiselectphotos;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.simple.eventbus.Subscriber;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.etong.android.frame.R;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;
import com.etong.android.frame.utils.logger.Logger;
import com.etong.android.frame.widget.TitleBar;

/**
 * @ClassName : AlbumUtils
 * @Description : 相册工具类,返回图片路径列表,支持多选图片,返回List<String>
 * @author : zhouxiqing
 * @date : 2016-4-11 上午10:19:12
 * 
 */
public class AlbumUtils extends BaseSubscriberActivity implements
		AdapterView.OnItemClickListener {
	public static final String TAG = "AlbumUtils";
	private static String tag = TAG;
	/** 可选图片数 */
	public static int selectNum = 9;

	private TitleBar mTitleBar;
	private ListView mListView;
	private AlbumListAdapter adapter;
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();

	/**
	 * @Title : startAlbumUtils
	 * @Description : 启动相册,设置TAG及图片数
	 * @params
	 * @param context
	 * @param tag
	 *            EventBus TAG
	 * @param selectNum
	 *            选择图片数(默认为9)
	 * @return void 返回类型
	 */
	public static void startAlbumUtils(Context context, String tag,
			int selectNum) {
		Intent intent = new Intent(context, AlbumUtils.class);
		intent.putExtra("tag", tag);
		if (selectNum <= 0) {
			Logger.e("selectNum must be greater than or equal to 1");
			return;
		}
		intent.putExtra("selectNum", selectNum);
		context.startActivity(intent);
	}

	@Override
	protected void onInit(@Nullable Bundle savedInstanceState) {
		setContentView(R.layout.activity_album_list);
		mTitleBar = new TitleBar(this);
		mTitleBar.setTitle("相册");
		mTitleBar.showBackButton(true);

		tag = this.getIntent().getStringExtra("tag");
		selectNum = this.getIntent().getIntExtra("selectNum", 9);
		mListView = (ListView) findViewById(R.id.pick_picture_total_list_view);
		mListView.setOnItemClickListener(this);

		getImages();
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		loadStart("加载中...", 0);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);
				if (mCursor == null || mCursor.getCount() == 0) {
					// 扫描图片失败
					mEventBus.post("", "getImages");
				} else {
					while (mCursor.moveToNext()) {
						// 获取图片的路径
						String path = mCursor.getString(mCursor
								.getColumnIndex(MediaStore.Images.Media.DATA));

						try {
							// 获取该图片的父路径名
							String parentName = new File(path).getParentFile()
									.getName();
							// 根据父路径名将图片放入到mGruopMap中
							if (!mGruopMap.containsKey(parentName)) {
								List<String> chileList = new ArrayList<String>();
								chileList.add(path);
								mGruopMap.put(parentName, chileList);
							} else {
								mGruopMap.get(parentName).add(path);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mCursor.close();
					// 通知Handler扫描图片完成
					mEventBus.post("", "getImages");

				}
			}
		}).start();

	}

	@Subscriber(tag = "getImages")
	public void getImages(String s) {
		loadFinish();
		if (mGruopMap.isEmpty()) {
			toastMsg("SD卡未就绪...");
			return;
		}
		adapter = new AlbumListAdapter(AlbumUtils.this,
				list = this.subGroupOfImage(mGruopMap), mListView, mDensity);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		List<String> childList = mGruopMap.get(list.get(position)
				.getFolderName());
		Intent intent = new Intent();
		intent.setClass(AlbumUtils.this, PickPictureActivity.class);
		intent.putExtra("albumName", list.get(position).getFolderName());
		intent.putStringArrayListExtra("data", (ArrayList<String>) childList);
		startActivity(intent);
	}

	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 *            相册HashMap
	 * @return List<ImageBean>
	 */
	@SuppressWarnings("unchecked")
	private List<ImageBean> subGroupOfImage(
			HashMap<String, List<String>> mGruopMap) {
		if (mGruopMap.size() == 0) {
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();

		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			SortPictureList sortList = new SortPictureList();
			Collections.sort(value, sortList);
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));// 获取该组的第一张图片

			list.add(mImageBean);
		}

		// 对相册进行排序，最近修改的相册放在最前面
		SortImageBeanComparator sortComparator = new SortImageBeanComparator(
				list);
		Collections.sort(list, sortComparator);

		return list;

	}

	@Subscriber(tag = TAG)
	public void onPickPictureFinish(List<String> list) {
		// 返回选择的图片列表
		mEventBus.post(list, tag);
		finish();
	}

	static class SortImageBeanComparator implements Comparator<ImageBean> {

		List<ImageBean> list;

		public SortImageBeanComparator(List<ImageBean> list) {
			this.list = list;
		}

		// 根据相册的第一张图片进行排序，最近修改的放在前面
		public int compare(ImageBean arg0, ImageBean arg1) {
			String path1 = arg0.getTopImagePath();
			String path2 = arg1.getTopImagePath();
			File f1 = new File(path1);
			File f2 = new File(path2);
			if (f1.lastModified() < f2.lastModified()) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
