package com.etong.android.frame.update;

import com.etong.android.frame.utils.logger.Logger;

/**
 * @ClassName : PatchUtils
 * @Description : APK Patch工具类
 * @author : zhouxiqing
 * @date : 2016-3-15 下午5:30:23
 */
public class PatchUtils {

	private static boolean canPatch;
	public static final int PATCH_SUCCESS = 0;
	public static final int PATCH_FAIL = -1;

	static {
		try {
			System.loadLibrary("ApkPatch");
			canPatch = true;
		} catch (Throwable localThrowable) {
			canPatch = false;
			System.out.println("loadLibrary : libApkPatch.so fail");
		}
	}

	/**
	 * 差分包与旧APK生成新APK
	 * 
	 * 返回：0，说明操作成功
	 * 
	 * @param oldApkPath
	 *            旧apk路径
	 * @param newApkPath
	 *            生成的新apk路径
	 * @param patchPath
	 *            差分包路径
	 * @return
	 */
	public static int bspatch(String oldApkPath, String newApkPath,
			String patchPath) {
		if (canPatch)
			try {
				return patch(oldApkPath, newApkPath, patchPath);
			} catch (Throwable localThrowable) {
				Logger.e(localThrowable, "差分包与旧APK生成新APK失败");
				return PATCH_FAIL;
			}
		return PATCH_FAIL;
	}

	public static boolean canPatch() {
		return canPatch;
	}

	/**
	 * native方法 差分包与旧APK生成新APK
	 * 
	 * 返回：0，说明操作成功
	 * 
	 * @param oldApkPath
	 *            旧apk路径
	 * @param newApkPath
	 *            生成的新apk路径
	 * @param patchPath
	 *            差分包路径
	 * @return
	 */
	public static native int patch(String oldApkPath, String newApkPath,
			String patchPath);
}