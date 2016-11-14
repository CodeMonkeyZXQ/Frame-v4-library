package com.etong.android.frame.library.camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import android.hardware.Camera;
import android.hardware.Camera.Size;

/**
 * @ClassName : CamParaUtil
 * @Description : 自定义相机尺寸工具类
 * @author : zhouxiqing
 * @date : 2016-5-30 下午3:44:03
 */
@SuppressWarnings("deprecation")
public class CamParaUtil {
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CamParaUtil myCamPara = null;

	private CamParaUtil() {

	}

	public static CamParaUtil getInstance() {
		if (myCamPara == null) {
			myCamPara = new CamParaUtil();
			return myCamPara;
		} else {
			return myCamPara;
		}
	}

	public Size getPropPreviewSize(List<Camera.Size> list, float th,
			int minWidth) {
		Collections.sort(list, sizeComparator);

		int i = 0;
		for (Size s : list) {
			if ((s.width >= minWidth) && equalRate(s, th,0.03f)) {
				break;
			}
			i++;
		}
		if (i == list.size()) {
			i=0;
			for (Size s : list) {
				if ((s.width >= minWidth) && equalRate(s, th,0.05f)) {
					break;
				}
				i++;
			}
		}
		if (i == list.size()) {
			i = list.size()-1;// 如果没找到，就选最大的size
		}
		return list.get(i);
	}

	public Size getPropPictureSize(List<Camera.Size> list, float th,
			int minWidth) {
		Collections.sort(list, sizeComparator);

		int i = 0;
		for (Size s : list) {
			if ((s.width >= minWidth) && equalRate(s, th,0.03f)) {
				break;
			}
			i++;
		}
		if (i == list.size()) {
			i=0;
			for (Size s : list) {
				if ((s.width >= minWidth) && equalRate(s, th,0.05f)) {
					break;
				}
				i++;
			}
		}
		if (i == list.size()) {
			i = list.size()-1;// 如果没找到，就选最大的size
		}
		return list.get(i);
	}

	public boolean equalRate(Size s, float rate,float tm) {
		float r = (float) (s.width) / (float) (s.height);
		if (Math.abs(r - rate) <= tm) {
			return true;
		} else {
			return false;
		}
	}

	public class CameraSizeComparator implements Comparator<Camera.Size> {
		public int compare(Size lhs, Size rhs) {
			if (lhs.width == rhs.width) {
				return 0;
			} else if (lhs.width > rhs.width) {
				return 1;
			} else {
				return -1;
			}
		}

	}
}
