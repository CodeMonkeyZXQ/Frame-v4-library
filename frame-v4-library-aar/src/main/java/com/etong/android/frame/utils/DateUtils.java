package com.etong.android.frame.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;

/**
 * @ClassName : DateUtils
 * @Description : 日期工具类
 * @author : zhouxiqing
 * @date : 2015-10-19 上午9:56:16
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {
	private static SimpleDateFormat format_ym = new SimpleDateFormat("yyyy-MM");
	private static SimpleDateFormat format_ymdhm = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static SimpleDateFormat format_ymdhm_str = new SimpleDateFormat(
			"yyyyMMddHHmm");
	private static Calendar calendar = new GregorianCalendar();
	@SuppressWarnings("unused")
	private static SimpleDateFormat date_format = new SimpleDateFormat(
			"yyyy-MM-dd");

	/**
	 * @Title : getCurrentSeconds
	 * @Description : 获得当前时间的时间戳
	 * @params
	 * @return long 返回类型
	 */
	public static long getCurrentSeconds() {
		Date date = new Date();
		return date.getTime() / 1000;
	}

	/**
	 * @Title : getCurrentTimes
	 * @Description : 得到系统当前时间的字符串
	 * @params
	 * @return String 格式: yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTimes() {
		return format_ymdhm.format(new Date());
	}

	/**
	 * @Title : getCurrentDateString
	 * @Description : 获得当前时间的字符串
	 * @params
	 * @return String 格式:yyyyMMddHHmm
	 */
	public static String getCurrentDateString() {
		return format_ymdhm_str.format(new Date());
	}

	/**
	 * @Title : getCurrentSecondsAfterDays
	 * @Description : 获得几天后的时间戳
	 * @params
	 * @return long 返回类型
	 */
	@SuppressWarnings("static-access")
	public static long getCurrentSecondsAfterDays(int day) {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, day);// 把日期往后增加day天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		return date.getTime() / 1000;
	}

	/**
	 * 获取某一年中的某一月的天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	static public int getDays(int year, int month) {
		String dateStr = year + "-";
		if (month > 10) {
			dateStr += month;
		} else {
			dateStr += "0" + month;
		}
		Date date = null;
		try {
			date = format_ym.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取某一月第一天是星期几
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	static public int getWeekOfMonthFirstDay(int year, int month) {
		String dateStr = year + "-";
		if (month > 10) {
			dateStr += month;
		} else {
			dateStr += "0" + month;
		}
		Date date = null;
		try {
			date = format_ym.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 计算两个日期相差的时间 日期格式:yyyy-MM-dd hh:mm
	 * 
	 * @param start
	 *            开始时间
	 * @param end
	 *            结束时间
	 * @return int[5] 时间差,分别是相差的年,月,日,时,分数
	 */
	static public int[] getDifferentDateValue(String start, String end) {
		int[] values = new int[5];
		Date start_date = null;
		Date end_date = null;
		Calendar start_calendar = Calendar.getInstance();
		Calendar end_calendar = Calendar.getInstance();
		try {
			start_date = format_ymdhm.parse(start);
			end_date = format_ymdhm.parse(end);
			start_calendar.setTime(start_date);
			end_calendar.setTime(end_date);

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		int start_year = start_calendar.get(Calendar.YEAR);
		System.out.println("start_year:" + start_year);
		int end_year = end_calendar.get(Calendar.YEAR);
		System.out.println("end_year:" + end_year);
		int start_month = start_calendar.get(Calendar.MONTH) + 1;
		System.out.println("start_month:" + start_month);
		int end_month = end_calendar.get(Calendar.MONTH) + 1;
		System.out.println("end_month:" + end_month);
		int start_day = start_calendar.get(Calendar.DATE);
		System.out.println("start_day:" + start_day);
		int end_day = end_calendar.get(Calendar.DATE);
		System.out.println("end_day:" + end_day);
		int start_hour = start_calendar.get(Calendar.HOUR_OF_DAY);
		System.out.println("start_hour:" + start_hour);
		int end_hour = end_calendar.get(Calendar.HOUR_OF_DAY);
		System.out.println("end_hour:" + end_hour);
		int start_minute = start_calendar.get(Calendar.MINUTE);
		System.out.println("start_minute:" + start_minute);
		int end_minute = end_calendar.get(Calendar.MINUTE);
		System.out.println("end_minute:" + end_minute);

		if (end_minute - start_minute < 0) {
			end_minute += 60;
			if (end_hour == 0) {
				end_hour = 23;
				if (end_day == 1) {
					if (end_month == 1) {
						end_year--;
						end_month = 12;
					} else {
						end_month--;
					}
					end_day = getDays(end_year, end_month);
				} else {
					end_day--;
				}
			} else {
				end_hour--;
			}
		}
		values[4] = end_minute - start_minute;

		if (end_hour - start_hour < 0) {
			end_hour += 24;
			if (end_day == 1) {
				if (end_month == 1) {
					end_year--;
					end_month = 12;
				} else {
					end_month--;
				}
				end_day = getDays(end_year, end_month);
			} else {
				end_day--;
			}
		}
		values[3] = end_hour - start_hour;

		if (end_day - start_day < 0) {
			if (end_month == 1) {
				end_year--;
				end_month = 12;
			} else {
				end_month--;
			}
			end_day += getDays(end_year, end_month);
		}
		values[2] = end_day - start_day;

		if (end_month - start_month < 0) {
			end_year--;
			end_month += 12;
		}
		values[1] = end_month - start_month;
		values[0] = end_year - start_year;
		return values;
	}

	/**
	 * @Title : compareTo
	 * @Description : 比较时间
	 * @params
	 * @param date1
	 *            格式:yyyy-MM-dd hh:mm
	 * @param date2
	 *            格式:yyyy-MM-dd hh:mm
	 * @return 返回值:{@link 1:date1>date2;0:date1=date2; -1:date<date2}
	 */
	@SuppressWarnings("deprecation")
	public static int compareTo(String date1, String date2) {
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format_ymdhm.parse(date1);
			d2 = format_ymdhm.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (d1.getYear() > d2.getYear()) {
			return 1;
		} else if (d1.getYear() == d2.getYear()) {
			if (d1.getMonth() > d2.getMonth()) {
				return 1;
			} else if (d1.getMonth() == d2.getMonth()) {
				if (d1.getDate() > d2.getDate()) {
					return 1;
				} else if (d1.getDate() == d2.getDate()) {
					return 0;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * 生成yyyy-MM-dd HH:mm格式的字符串
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static String getFormatDate(int year, int month, int day, int hour,
			int minute) {
		String selectDate = year + "-";
		if (month < 10) {
			selectDate += "0" + month + "-";
		} else {
			selectDate += month + "-";
		}

		if (day < 10) {
			selectDate += "0" + day + " ";
		} else {
			selectDate += day + " ";
		}

		if (hour < 10) {
			selectDate += "0" + hour + ":";
		} else {
			selectDate += hour + ":";
		}
		if (minute < 10) {
			selectDate += "0" + minute;
		} else {
			selectDate += minute;
		}
		return selectDate;
	}

	/**
	 * @Title : getDateStringFromSeconds
	 * @Description : 时间戳转字符串
	 * @params
	 * @param seconds
	 * @return 格式:yyyy-MM-dd HH:mm
	 */
	public static String getDateStringFromSeconds(long seconds) {
		long s = seconds * 1000;
		Date date = new Date(s);
		return format_ymdhm.format(date);
	}

	/**
	 * @Title : getSecondsFormDateString
	 * @Description : 字符串时间转时间戳
	 * @params
	 * @param dateString
	 * @return 设定文件
	 * @return long 返回类型
	 */
	public static long getSecondsFormDateString(String dateString) {
		Date date = null;
		try {
			date = format_ymdhm.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
		return date.getTime() / 1000;
	}

	/**
	 * @Title : getDateFormString
	 * @Description : String类型的时间转Calendar
	 * @params
	 * @param dateStr
	 * @return 设定文件
	 * @return Calendar 返回类型
	 */
	public static Calendar getDateFormString(String dateStr) {
		Calendar calendar = null;
		try {
			Date date = format_ymdhm.parse(dateStr);
			calendar = Calendar.getInstance();
			calendar.setTime(date);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return calendar;
	}

}
