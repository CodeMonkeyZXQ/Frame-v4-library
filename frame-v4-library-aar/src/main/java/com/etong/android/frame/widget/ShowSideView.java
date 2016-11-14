package com.etong.android.frame.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.simple.eventbus.EventBus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.etong.android.frame.R;
import com.etong.android.frame.utils.ImageProvider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName : ShowSideView
 * @Description : 实现图片轮播工具类
 * @author : zhouxiqing
 * @date : 2015-10-23 上午10:16:25
 * 
 */
@SuppressLint("HandlerLeak")
public class ShowSideView extends FrameLayout {
	private static String tag;

	private int mWinWidth = 0;
	private View root = null;

	// 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.9.4.jar
	private ImageLoader imageLoader = null;//ImageLoader.getInstance();

	// 轮播图图片数量
	@SuppressWarnings("unused")
	private final static int IMAGE_COUNT = 5;
	// 自动轮播的时间间隔
	@SuppressWarnings("unused")
	private final static int TIME_INTERVAL = 5;
	// 自动轮播启用开关
	private final static boolean isAutoPlay = true;

	// 自定义轮播的资源
	private List<ShowSideViewData> sideData;
	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;
	// 放圆点的View的list
	private List<View> dotViewsList;

	private ViewPager viewPager;
	// 当前轮播页
	private int currentItem = 0;
	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;

	private Context context;

	// Handler
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);
		}
	};

	public ShowSideView(Context context) {
		this(context, null);
	}

	public ShowSideView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShowSideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		EventBus.getDefault().register(context);
//		initImageLoader(context);
		
		imageLoader=ImageProvider.getInstance().getImageLoader();

		// initData();
		if (isAutoPlay) {
			startPlay();
		}
	}

	/**
	 * @Title : setImgUrl
	 * @Description : 设置图片url,没有点击响应
	 * @params
	 * @param imageUrls
	 *            图片地址
	 * @return void 返回类型
	 * @throws
	 */
	public void setImgUrl(List<String> images) {
		if (null == sideData) {
			sideData = new ArrayList<ShowSideViewData>();
			imageViewsList = new ArrayList<ImageView>();
			dotViewsList = new ArrayList<View>();
		} else {
			sideData.clear();
			imageViewsList.clear();
			dotViewsList.clear();
		}
		for (int i = 0; i < images.size(); i++) {
			ShowSideViewData data = new ShowSideViewData();
			data.setData(i);
			data.setUrl(images.get(i));
			sideData.add(data);
		}
		initUI(context);
	}

	/**
	 * @Title : setImageData
	 * @Description : 设置轮播数据,包含图片url及id,有点击响应,用EventBus接收事件,参数为String型id
	 * @params
	 * @param sideData
	 * @param tag
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("static-access")
	public void setImageData(List<ShowSideViewData> data, String tag) {
		this.tag = tag;
		if (null == sideData) {
			sideData = new ArrayList<ShowSideViewData>();
			imageViewsList = new ArrayList<ImageView>();
			dotViewsList = new ArrayList<View>();
		} else {
			sideData.clear();
			imageViewsList.clear();
			dotViewsList.clear();
		}
		if (data != null) {
			this.sideData = data;
		}
		initUI(context);
	}

	/**
	 * @Title : startPlay
	 * @Description : 开始轮播图切换
	 * @params 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
				TimeUnit.SECONDS);
	}

	/**
	 * @Title : initUI
	 * @Description : 初始化Views等UI
	 * @params
	 * @param context
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private void initUI(Context context) {
		if (sideData == null || sideData.size() == 0)
			return;

		root = LayoutInflater.from(context).inflate(R.layout.layout_slideshow,
				this, true);

		LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();

		// 热点个数与图片个数相等
		for (int i = 0; i < sideData.size(); i++) {
			ImageView view = new ImageView(context);
			view.setTag(sideData.get(i).getUrl());
			if (i == 0)// 给一个默认图
				view.setBackgroundResource(R.drawable.default_banner);
			imageViewsList.add(view);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ImageView dotView = new ImageView(context);
			params.leftMargin = 4;
			params.rightMargin = 4;
			dotLayout.addView(dotView, params);
			dotViewsList.add(dotView);

		}

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);

		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

		// 获取屏幕像素相关信息
		WindowManager mWm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mWinWidth = mWm.getDefaultDisplay().getWidth();
	}

	/**
	 * @Title : initImageLoader
	 * @Description : ImageLoader 图片组件初始化
	 * @params
	 * @param context
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
//	@SuppressWarnings("deprecation")
//	public static void initImageLoader(Context context) {
//		// This configuration tuning is custom. You can tune every option, you
//		// may tune some of them,
//		// or you can create default configuration by
//		// ImageLoaderConfiguration.createDefault(this);
//		// method.
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//				context).threadPriority(Thread.NORM_PRIORITY - 2)
//				.denyCacheImageMultipleSizesInMemory()
//				.discCacheFileNameGenerator(new Md5FileNameGenerator())
//				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs() // Remove
//									// for
//									// release
//									// app
//				.build();
//		// Initialize ImageLoader with configuration.
//		ImageLoader.getInstance().init(config);
//	}

	/**
	 * @ClassName : MyPagerAdapter
	 * @Description : 填充ViewPager的页面适配器
	 * @author : zhouxiqing
	 * @date : 2015-10-23 上午10:43:29
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			// ((ViewPag.er)container).removeView((View)object);
			((ViewPager) container).removeView(imageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageView imageView = imageViewsList.get(position);
			imageLoader.displayImage(imageView.getTag() + "", imageView,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							if (arg2 == null) {
								return;
							}
							int with = arg2.getWidth() - 2;
							int height = arg2.getHeight();
							root.getLayoutParams().width = mWinWidth;
							root.getLayoutParams().height = mWinWidth * height
									/ with;
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});

			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(
							sideData.get(currentItem).getData(), tag);
				}
			});
			((ViewPager) container).addView(imageViewsList.get(position));
			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}

	}

	/**
	 * 
	 * @ClassName : MyPageChangeListener
	 * @Description : ViewPager的监听器,当ViewPager中页面的状态发生改变时调用
	 * @author : zhouxiqing
	 * @date : 2015-10-23 上午10:44:06
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			case 1:// 手势滑动，空闲中
				isAutoPlay = false;
				break;
			case 2:// 界面切换中
				isAutoPlay = true;
				break;
			case 0:// 滑动结束，即切换完毕或者加载完毕
					// 当前为最后一张，此时从右向左滑，则切换到第一张
				if (viewPager.getCurrentItem() == viewPager.getAdapter()
						.getCount() - 1 && !isAutoPlay) {
					viewPager.setCurrentItem(0);
				}
				// 当前为第一张，此时从左向右滑，则切换到最后一张
				else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
					viewPager
							.setCurrentItem(viewPager.getAdapter().getCount() - 1);
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int pos) {
			currentItem = pos;
			for (int i = 0; i < dotViewsList.size(); i++) {
				if (i == pos) {
					((View) dotViewsList.get(pos))
							.setBackgroundResource(R.drawable.dot_sel);
				} else {
					((View) dotViewsList.get(i))
							.setBackgroundResource(R.drawable.dot_dis);
				}
			}
		}

	}

	/**
	 * @ClassName : SlideShowTask
	 * @Description : 执行轮播图切换任务
	 * @author : zhouxiqing
	 * @date : 2015-10-23 上午10:44:29
	 * 
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViewsList.size();
				handler.obtainMessage().sendToTarget();
			}
		}
	}
}