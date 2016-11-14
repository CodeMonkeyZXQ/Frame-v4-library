package com.etong.android.frame.library.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

/**
 * @ClassName : CameraSurfaceView
 * @Description : 自定义SurfaceView
 * @author : zhouxiqing
 * @date : 2016-5-30 下午3:43:17
 */

public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	CameraInterface mCameraInterface;
	Context mContext;
	SurfaceHolder mSurfaceHolder;
	float previewRate = 1.3333f;
	private Camera mCamera;

	@SuppressWarnings("deprecation")
	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);// translucent半透明
															// transparent透明
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}
	@SuppressWarnings("deprecation")
	public CameraSurfaceView(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);// translucent半透明
		// transparent透明
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		CameraInterface.getInstance().doOpenCamera();
		LayoutParams params = this.getLayoutParams();
		//params.width = (int) ((float) this.getHeight() / previewRate);
		params.width = (int) ((float) this.getWidth() );
		params.height = (int) (params.width * previewRate);
		
		
		this.setLayoutParams(params);
	}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, int format,
			final int width, final int height) {
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		CameraInterface.getInstance().doStopCamera();
	}

	public SurfaceHolder getSurfaceHolder() {
		return mSurfaceHolder;
	}

}
