package com.etong.android.frame.update;

/**
 * @ClassName : AppUpdateResultAction
 * @Description : 更新模块回调
 * @author : zhouxiqing
 * @date : 2016-4-18 下午1:54:34
 * 
 */
public abstract class AppUpdateResultAction {

	public AppUpdateResultAction() {
	}

	/**
	 * @Title : noUpdate
	 * @Description : 无更新时的操作
	 * @params 设定文件
	 * @return void 返回类型
	 */
	public abstract void noUpdate();

	/**
	 * @Title : fail
	 * @Description : 更新失败时的操作
	 * @params
	 * @param errCode
	 *            错误码（见AppUpdateProvider.ERR_NULL;AppUpdateProvider.ERR_NETWORK;
	 *            AppUpdateProvider.ERR_CANCLE;AppUpdateProvider.ERR_LATER）
	 * @param errStr
	 *            错误内容
	 * @return void 返回类型
	 */
	public abstract void fail(int errCode, String errStr);

    public abstract void haveUpdate();
}
