package com.etong.android.frame.library.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @ClassName : ImageUtil
 * @Description : 旋转Bitmap工具类
 * @author : zhouxiqing
 * @date : 2016-5-30 下午3:49:05
 */
public class ImageUtil {
	/**
	 * @Title : getRotateBitmap
	 * @Description : 旋转Bitmap
	 * @params
	 * @param b
	 *            Bitmap
	 * @param rotateDegree
	 *            旋转角度
	 * @return 设定文件
	 * @return Bitmap 返回类型
	 */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}
}
