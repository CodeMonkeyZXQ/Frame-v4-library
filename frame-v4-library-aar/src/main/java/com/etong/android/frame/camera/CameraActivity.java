package com.etong.android.frame.camera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.etong.android.frame.R;
import com.etong.android.frame.utils.logger.Logger;

public class CameraActivity extends Activity {

	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_camera);
		imageView = (ImageView) this.findViewById(R.id.preview);

		Button button = (Button) this.findViewById(R.id.cameraButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(), "camera.jpg")));
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			this.imageView.setImageDrawable(Drawable.createFromPath(new File(
					Environment.getExternalStorageDirectory(), "camera.jpg")
					.getAbsolutePath()));
			Logger.v("cam.demo"+ "====>" + data);

		}
	}

}
