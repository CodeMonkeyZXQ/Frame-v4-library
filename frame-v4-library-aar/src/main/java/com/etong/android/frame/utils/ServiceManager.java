package com.etong.android.frame.utils;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

/**
 * @ClassName : ServiceManager
 * @Description : 获得系统服务管理器
 * @author : zhouxiqing
 * @date : 2015-10-20 上午9:45:01
 * 
 */
public class ServiceManager {

	private static ConnectivityManager cm;
	private static LocationManager locationManager;
	private static TelephonyManager telephonyManager;
	private static InputMethodManager inputMethodManager;
	private static Vibrator vibrator;
	private static SensorManager sensorManager;
	private static Sensor accelerometerSensor;
	private static Sensor lightSensor;// 光线传感器引用
	private static LayoutInflater inflater;

	private ServiceManager() {
	};

	/**
	 * @Title : getConnectivityManager
	 * @Description : 获得ConnectivityManager
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return ConnectivityManager 返回类型
	 */
	public static ConnectivityManager getConnectivityManager(Context context) {

		if (cm == null) {
			synchronized (ServiceManager.class) {
				if (cm == null) {
					cm = (ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE);
				}
			}
		}
		return cm;
	}

	/**
	 * @Title : getLocationManager
	 * @Description : 获得LocationManager
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return LocationManager 返回类型
	 */
	public static LocationManager getLocationManager(Context context) {
		if (locationManager == null) {
			synchronized (ServiceManager.class) {
				if (locationManager == null) {
					locationManager = ((LocationManager) context
							.getSystemService(Context.LOCATION_SERVICE));
				}
			}
		}
		return locationManager;
	}

	/**
	 * @Title : getTelephonyManager
	 * @Description : 获得TelephonyManager
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return TelephonyManager 返回类型
	 */
	public static TelephonyManager getTelephonyManager(Context context) {
		if (telephonyManager == null) {
			synchronized (ServiceManager.class) {
				if (telephonyManager == null) {
					telephonyManager = ((TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE));
				}
			}
		}
		return telephonyManager;
	}

	/**
	 * @Title : getInputMethodManager
	 * @Description : 获得InputMethodManager
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return InputMethodManager 返回类型
	 */
	public static InputMethodManager getInputMethodManager(Context context) {
		if (inputMethodManager == null) {
			synchronized (ServiceManager.class) {
				if (inputMethodManager == null) {
					inputMethodManager = ((InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE));
				}
			}
		}
		return inputMethodManager;
	}

	/**
	 * @Title : getVibrator
	 * @Description : 获得震动的控制器
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return Vibrator 返回类型
	 */
	public static Vibrator getVibrator(Context context) {
		if (vibrator == null) {
			synchronized (ServiceManager.class) {
				if (vibrator == null) {
					vibrator = (Vibrator) context
							.getSystemService(Service.VIBRATOR_SERVICE);
				}
			}
		}
		return vibrator;
	}

	/**
	 * @Title : getSensorManager
	 * @Description : 获得传感器管理器
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return SensorManager 返回类型
	 */
	public static SensorManager getSensorManager(Context context) {
		if (sensorManager == null) {
			synchronized (ServiceManager.class) {
				if (sensorManager == null) {
					sensorManager = (SensorManager) context
							.getSystemService(Context.SENSOR_SERVICE);
				}
			}
		}
		return sensorManager;
	}

	/**
	 * @Title : getAccelerometerSensor
	 * @Description : 获得加速管理器
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return Sensor 返回类型
	 */
	public static Sensor getAccelerometerSensor(Context context) {
		if (accelerometerSensor == null) {
			synchronized (ServiceManager.class) {
				if (accelerometerSensor == null) {
					accelerometerSensor = getSensorManager(context)
							.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				}
			}
		}
		return accelerometerSensor;
	}

	/**
	 * @Title : getLightSensor
	 * @Description : 获得环境光照传感器
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return Sensor 返回类型
	 */
	public static Sensor getLightSensor(Context context) {
		if (lightSensor == null) {
			synchronized (ServiceManager.class) {
				if (lightSensor == null) {
					lightSensor = getSensorManager(context).getDefaultSensor(
							Sensor.TYPE_LIGHT);
				}
			}
		}
		return lightSensor;
	}

	/**
	 * @Title : getLayoutInflate
	 * @Description : 获得LayoutInflater
	 * @params
	 * @param context
	 * @return 设定文件
	 * @return LayoutInflater 返回类型
	 */
	public static LayoutInflater getLayoutInflate(Context context) {
		if (inflater == null) {
			synchronized (ServiceManager.class) {
				if (inflater == null) {
					inflater = LayoutInflater.from(context);
				}
			}
		}
		return inflater;
	}

}
