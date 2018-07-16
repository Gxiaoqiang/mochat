/*
 * Copyright (c)Shanghai Easy Deal Foreign Currency Exchange Co., Ltd. 
 */
package com.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期共通类
 *
 * @author zhang
 * @date 2014-12-19
 *
 */
public class DateUtil extends DateUtils {

	public final static String PATTERN_SIMPLE = "yyyy-MM-dd";

	public final static String PATTERN_SIMPLE_EXT = "yyyy.MM.dd";

	public final static String PATTERN_WHOLE = "yyyy-MM-dd HH:mm:ss";

	public final static String PATTERN_COMPACT = "yyyyMMddHHmmss";
	
	public final static String PATTERN_COMPACT_MSEC = "yyyyMMddHHmmsssss";

	public final static String PATTERN_SUCCINCT = "yyyyMMdd";

	/**
	 * 字符串转日期
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static Date strTodate(String str, String pattern) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			// 不能转换时返回null
		}
		return date;
	}

	/**
	 * 获取当前时间前几个月的时间
	 * 
	 * @param mouths
	 *            月份
	 * @return
	 */
	public static String findDateFrom(int mouths, String format) {

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MONTH, -mouths);

		Date findDate = calendar.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat(format);

		return sdf.format(findDate);
	}

	public static Date searchWeekDate(int days, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
		String day = calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DATE);
		return DateUtil.strTodate(day, format);
	}

	/**
	 * 将日期转化成yyyy-mm-dd格式的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		return sdf.format(date);
	}

	/**
	 * 取2个时间相差的天数
	 * @param date1
	 * @param date2
	 * @param isAbs    true:取绝对值 false:差值可能是负数
	 * @return
	 * @throws Exception
	 */
	public static long getBetweenDiffDay(Date date1, Date date2, boolean isAbs) throws Exception {
		long date1Value = date1.getTime();
		long date2Value = date2.getTime();
		long diff = (date1Value - date2Value) / (24 * 3600 * 1000);
		if (isAbs) {
			return Math.abs(diff);
		} else {
			return diff;
		}
	}
	
}
