package com.etong.android.frame.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import com.etong.android.frame.R;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;

@SuppressWarnings("deprecation")
public class AlbumActivity extends BaseSubscriberActivity implements
		OnItemSelectedListener, ViewFactory {
	private ImageSwitcher is;
	private Gallery gallery;

	private Integer[] mThumbIds = { android.R.drawable.ic_dialog_info,
			android.R.drawable.ic_dialog_info, android.R.drawable.ic_dialog_info,
			android.R.drawable.ic_dialog_info, android.R.drawable.ic_dialog_info, };

	private Integer[] mImageIds = { android.R.drawable.ic_dialog_info,
			android.R.drawable.ic_dialog_info, android.R.drawable.ic_dialog_info,
			android.R.drawable.ic_dialog_info, android.R.drawable.ic_dialog_info, };

	@Override
	public View makeView() {
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			i.setImageResource(mThumbIds[position]);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			i.setBackgroundResource(android.R.drawable.ic_dialog_info);
			return i;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		is.setImageResource(mImageIds[position]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	protected void onInit(@Nullable Bundle savedInstanceState) {
		setContentView(R.layout.activity_album);

		is = (ImageSwitcher) findViewById(R.id.switcher);
		is.setFactory(this);

		is.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		is.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		gallery = (Gallery) findViewById(R.id.gallery);

		gallery.setAdapter(new ImageAdapter(this));
		gallery.setOnItemSelectedListener(this);

	}

}