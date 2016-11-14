package com.etong.android.frame.library.camera;

import android.graphics.Bitmap;
import android.os.Environment;

import com.etong.android.frame.utils.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName : FileUtil
 * @Description : 文件存储工具类
 * @author : zhouxiqing
 * @date : 2016-5-30 下午3:48:17
 */
public class FileUtil {
	private static final File parentPath = Environment
			.getExternalStorageDirectory();
	private static String storagePath = "";
	private static final String DST_FOLDER_NAME = "Etong";

	/**
	 * @Title : initPath
	 * @Description : 初始化保存路径
	 * @params
	 * @return 设定文件
	 * @return String 返回类型
	 */
	private static String initPath() {
		if (storagePath.equals("")) {
			storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		return storagePath;
	}

	/**
	 * @Title : saveBitmap
	 * @Description : 保存Bitmap到sdcard
	 * @params
	 * @param b
	 *            Bitmap
	 * @return void 返回类型
	 */
	public static String saveBitmap(Bitmap b) {

		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake + ".jpg";
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Logger.i("saveBitmap成功,jpegName = " + jpegName);
		} catch (IOException e) {
			Logger.e(e, "saveBitmap:失败");
		}
		return jpegName;
	}

}
