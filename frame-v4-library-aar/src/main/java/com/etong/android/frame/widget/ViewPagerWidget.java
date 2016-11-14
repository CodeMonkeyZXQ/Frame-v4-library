package com.etong.android.frame.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.etong.android.frame.R;

/**
 * @ClassName : ViewPagerWidget
 * @Description : 实现图片轮播工具类
 * @author : zhouxiqing
 * @param <T>
 * @date : 2015-10-23 上午10:16:25
 * 
 */
@SuppressLint("HandlerLeak")
public abstract class ViewPagerWidget extends FrameLayout {

	private Object data;
	private int resource;

	// 图片数量
	@SuppressWarnings("unused")
	private final static int IMAGE_COUNT = 5;

	// 放轮播图片的ImageView 的list
	private List<View> pageViewsList;
	// 放圆点的View的list
	private List<View> dotViewsList;

	private ViewPager viewPager;
	private Context context;

	public ViewPagerWidget(Context context, int resource) {
		this(context, resource, null);
		this.resource = resource;
	}

	public ViewPagerWidget(Context context, int resource, AttributeSet attrs) {
		this(context, resource, attrs, 0);
		this.resource = resource;
	}

	public ViewPagerWidget(Context context, int resource, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.resource = resource;

		initUI(context);
	}

	/**
	 * @Title : initUI
	 * @Description : 初始化Views等UI
	 * @params
	 * @param context
	 *            设定文件
	 * @return void 返回类型
	 */
	@SuppressWarnings("deprecation")
	private void initUI(Context context) {
		pageViewsList = new ArrayList<View>();
		dotViewsList = new ArrayList<View>();
		
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);

		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}

	public void add(Object data) {
		this.data = data;

		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
				true);

		LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);

		ImageView view = new ImageView(context);
		view.setScaleType(ScaleType.FIT_XY);
		dotViewsList.add(view);

		ImageView dotView = new ImageView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = 4;
		params.rightMargin = 4;
		dotLayout.addView(dotView, params);
		dotViewsList.add(dotView);
	}

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
			((ViewPager) container).removeView(pageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View view = pageViewsList.get(position);
			if(null==view){
				LayoutInflater inflater = LayoutInflater.from(context);
				view = inflater.inflate(resource, null);
				onPaint(view, data, position);
			}

			((ViewPager) container).addView(pageViewsList.get(position));
			return pageViewsList.get(position);
		}

		@Override
		public int getCount() {
			return pageViewsList.size();
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

	abstract protected void onPaint(View view, Object data, int position);
}