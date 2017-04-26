/**   
 * @Title: StoragePathUtils.java
 * @Package com.etong.ezviz.utils
 * @Description: 用于获取有效的存储路径的工具类
 * @author 周锡清(ZhouXiqing)
 * @date 2015-9-16 下午4:18:21
 * @version V1.0
 */
package com.etong.android.frame.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.etong.android.frame.utils.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StoragePathUtils {
	private static List<Storages> jsonStorage = null;
	public static final long PIC_MIN_MEM_SPACE = 10485760L;
	public static final long REC_MIN_MEM_SPACE = 20971520L;

	/**
	 * @Title : getStoragePaths
	 * @Description : 获取手机存储路径
	 * @params
	 * @param cxt
	 * @return 设定文件
	 * @return String[] 存储路径
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static String[] getStoragePaths(Context cxt) {
		List<String> pathsList = new ArrayList<String>();
		String sdCard = null;
		if (Environment.getExternalStorageState().equals("mounted")) {
			sdCard = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			pathsList.add(0, sdCard);
		}
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
			pathsList.addAll(getSDCardPath());
		} else {
			StorageManager storageManager = (StorageManager) cxt.getApplicationContext()
					.getSystemService(Context.STORAGE_SERVICE);
			try {
				Method method = StorageManager.class
						.getDeclaredMethod("getVolumePaths");
				method.setAccessible(true);
				Object result = method.invoke(storageManager);
				if (result != null && result instanceof String[]) {
					String[] pathes = (String[]) result;
					StatFs statFs;
					for (String path : pathes) {
						if (!TextUtils.isEmpty(path) && new File(path).exists()) {
							statFs = new StatFs(path);
							if (statFs.getBlockCount() * statFs.getBlockSize() != 0) {
								if (!path.equals(sdCard)) {
									pathsList.add(path);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				File externalFolder = Environment.getExternalStorageDirectory();
				if (externalFolder != null) {// 排除无SD卡的情况
					pathsList.add(externalFolder.getAbsolutePath());
				}
			}
		}
		return pathsList.toArray(new String[pathsList.size()]);
	}

	/**
	 * 
	 * @Title: getStorageRemainSize
	 * @Description: 获取手机存储路径,可用空间及总空间
	 * @param @param cxt
	 * @param @return
	 * @return List<JSONObject>
	 */
	@SuppressWarnings("deprecation")
	public static List<Storages> getStorageInfo(Context cxt) {
		jsonStorage = new ArrayList<Storages>();
		Storages sd = null;
		long blockSize;
		String paths[] = getStoragePaths(cxt);
		for (int i = 0; i < paths.length; i++) {
			StatFs statfs = new StatFs(paths[i]);
			blockSize = statfs.getBlockSize();
			sd = new Storages();
			sd.setPath(paths[i]);
			sd.setFreeSize(blockSize * statfs.getAvailableBlocks());// 可用空间
			sd.setTotalSize(blockSize * statfs.getBlockCount());// 总空间
			jsonStorage.add(i, sd);
		}
		return jsonStorage;
	}

	@SuppressWarnings("deprecation")
	private static List<String> getSDCardPath() {
		List<String> pathsList = new ArrayList<String>();
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();
		BufferedInputStream in = null;
		BufferedReader inBr = null;
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			in = new BufferedInputStream(p.getInputStream());
			inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				Logger.i(lineStr);
				if (lineStr.contains("sdcard")) {
					String[] strArray = lineStr.split(" ");
					if (strArray != null && strArray.length >= 5) {
						String path = strArray[1].replace("/.android_secure",
								"");
						StatFs statFs;
						if (!TextUtils.isEmpty(path) && new File(path).exists()) {
							statFs = new StatFs(path);
							if (statFs.getBlockCount() * statFs.getBlockSize() != 0) {
								pathsList.add(path);
							}
						}
					}
				}
				// 检查命令是否执行失败。
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0表示正常结束，1：非正常结束
					Logger.e("命令执行失败!");
				}
			}
		} catch (Exception e) {
			Logger.e(e,"getSDCardPath error");
			// return Environment.getExternalStorageDirectory().getPath();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (inBr != null) {
					inBr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (pathsList.size() == 0) {
			File externalFolder = Environment.getExternalStorageDirectory();
			if (externalFolder != null) {// 排除无SD卡的情况
				pathsList.add(externalFolder.getAbsolutePath());
			}
		}
		return pathsList;
	}
}