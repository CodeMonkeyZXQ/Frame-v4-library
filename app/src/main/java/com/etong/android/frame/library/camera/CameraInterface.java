package com.etong.android.frame.library.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import com.etong.android.frame.utils.logger.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName : CameraInterface
 * @Description : 自定义相机接口类
 * @author : zhouxiqing
 * @date : 2016-5-30 下午3:42:16
 */
@SuppressWarnings("deprecation")
public class CameraInterface {
	private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean isPreviewing = false;
	private static CameraInterface mCameraInterface;
	private static TakePhoteCallBack mTakePhotoCallback;

	private CameraInterface() {

	}

	public static synchronized CameraInterface getInstance() {
		if (mCameraInterface == null) {
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}

	/**
	 * 打开Camera
	 */
	public void doOpenCamera() {
		try {
			mCamera = Camera.open();
		} catch (Exception e) {
			Logger.e(e, "open camera is fail");
		}
		if (mCamera == null) {
			Logger.e("open camera is fail,mCamera is null");
		}
	}

	/**
	 * 开启预览
	 * 
	 * @param holder
	 * @param previewRate
	 */
	public Size doStartPreview(SurfaceHolder holder,
			float previewRate) {
		if (isPreviewing) {
//			mCamera.stopPreview();
			return null;
		}
		if (mCamera != null) {

			mParams = mCamera.getParameters();
			mParams.setPictureFormat(PixelFormat.JPEG);// 设置拍照后存储的图片格式
			// 设置PreviewSize和PictureSize
			Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(), previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);

			mCamera.setDisplayOrientation(90);

			List<String> focusModes = mParams.getSupportedFocusModes();
			if (focusModes.contains("continuous-video")) {
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			mCamera.setParameters(mParams);

			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();// 开启预览
			} catch (IOException e) {
				e.printStackTrace();
			}

			isPreviewing = true;
			mParams = mCamera.getParameters(); // 重新get一次
			Logger.i("最终设置:PreviewSize--With = "
					+ mParams.getPreviewSize().width + "Height = "
					+ mParams.getPreviewSize().height
					+ "\n最终设置:PictureSize--With = "
					+ mParams.getPictureSize().width + "Height = "
					+ mParams.getPictureSize().height);
			return previewSize;
		}
		return null;
	}

	/**
	 * 停止预览，释放Camera
	 */
	public void doStopCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			isPreviewing = false;
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 拍照
	 */
	public void doTakePicture(TakePhoteCallBack callBack) {
		mTakePhotoCallback = callBack;
		if (isPreviewing && (mCamera != null)) {
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
		}
	}

	/* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
	ShutterCallback mShutterCallback = new ShutterCallback() {
		// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
		public void onShutter() {
			Logger.i("myShutterCallback:onShutter...");
		}
	};
	PictureCallback mRawCallback = new PictureCallback() {
		// 拍摄的未压缩原数据的回调,可以为null
		public void onPictureTaken(byte[] data, Camera camera) {
			Logger.i("myRawCallback:onPictureTaken...");

		}
	};
	PictureCallback mJpegPictureCallback = new PictureCallback() {
		// 对jpeg图像数据的回调,最重要的一个回调
		public void onPictureTaken(byte[] data, Camera camera) {
			Logger.i("myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if (null != data) {
				b = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;
			}
			// 保存图片到sdcard
			if (null != b) {
				// 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation",
				// 90)失效。
				// 图片竟然不能旋转了，故这里要旋转下
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
				String path = FileUtil.saveBitmap(rotaBitmap);
				mTakePhotoCallback.Data(rotaBitmap, path);
			}
			// 再次进入预览
			mCamera.startPreview();
			isPreviewing = true;
		}
	};

	/**
	 * @ClassName : TakePhoteCallBack
	 * @Description : 拍照回调
	 * @author : zhouxiqing
	 * @date : 2016-5-30 下午4:53:26
	 * 
	 */
	public interface TakePhoteCallBack {
		/**
		 * @Title : Data
		 * @Description : 拍照回调
		 * @params
		 * @param bitmap
		 *            图片Bitmap数据
		 * @param path
		 *            图片存储路径
		 * @return void 返回类型
		 */
		public void Data(Bitmap bitmap, String path);
	}

}
