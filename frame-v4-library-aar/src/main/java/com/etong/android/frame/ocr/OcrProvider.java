package com.etong.android.frame.ocr;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.etong.android.frame.common.BaseHttpUri;
import com.etong.android.frame.event.CommonEvent;
import com.etong.android.frame.publisher.HttpMethod;
import com.etong.android.frame.publisher.HttpPublisher;
import com.etong.android.frame.utils.Base64;
import com.etong.android.frame.utils.logger.Logger;

/**
* @ClassName    : OcrProvider 
* @Description  : OCR 
* @author       : yuanjie
* @date         : 2016-3-21 下午3:29:47 
 */
public class OcrProvider {
	private HttpPublisher mHttpPublisher;

	private static class Holder{
		private static final OcrProvider INSTANCE = new OcrProvider();
	}
	private OcrProvider() {

	}

	public static OcrProvider getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * @Title : ocr
	 * @Description : 将一张图片上的提交到百度服务器进行识别
	 * @params
	 * @param image
	 *            设定文件
	 * @return void 返回类型
	 */
	@SuppressWarnings("static-access")
	public void ocr(String image) {
		Map<String, String> map = new HashMap<String, String>();

		map.put("fromdevice", "android");
		map.put("clientip", "10.10.10.0");
		map.put("detecttype", "LocateRecognize");
		map.put("languagetype", "CHN_ENG");
		map.put("imagetype", "1");
		String base64Image = "";

		Base64 base = new Base64();

		FileInputStream fs;
		try {
			fs = new FileInputStream(image);
			BufferedInputStream bs = new BufferedInputStream(fs);

			byte read[] = new byte[1];
			do {
				read[0] = (byte) bs.read();
				base64Image += base.encode(read);
			} while (read[0] > 0);

			map.put("image", base64Image);

			HttpMethod method = new HttpMethod(BaseHttpUri.URL_OCR, map);
			mHttpPublisher.sendRequest(method, CommonEvent.OCR);
			bs.close();
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
			Logger.e(e,"Image io err");
		}

	}
}
