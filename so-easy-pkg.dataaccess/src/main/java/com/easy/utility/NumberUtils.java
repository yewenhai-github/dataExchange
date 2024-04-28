package com.easy.utility;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

	private static final Pattern INT_PATTERN = Pattern.compile("^-?\\d+$");

	private static final Pattern FLOAT_PATTERN = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");

	private static final DecimalFormat FORMAT = new DecimalFormat(".############");

	/**
	 * 将字符串转换成int
	 * 
	 * @param value
	 *            字符串
	 * @return int 转换结果
	 */
	public static int parseInt(String value) {
		if (SysUtility.isEmpty(value)) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	/**
	 * 将字符串转换成int
	 * 
	 * @param value
	 *            字符串
	 * @param defaultValue
	 *            转换发生错误时的默认值
	 * @return int 转换结果
	 */
	public static int parseInt(String value, int defaultValue) {
		try {
			return parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将字符转换为double
	 * @param value
	 * @return
	 */
	public static double parseDouble(String value) {
		if (SysUtility.isEmpty(value)) {
			return 0d;
		}
		return Double.parseDouble(value);
	}

	/**
	 * 将字符转换为double
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static double parseDouble(String value, double defaultValue) {
		try {
			return parseDouble(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 判断是否为整数型
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Matcher matcher = INT_PATTERN.matcher(str);
		return matcher.find();
	}

	/**
	 * 判断是否为浮点型
	 * @param str
	 * @return
	 */
	public static boolean isFloat(String str) {
		Matcher matcher = FLOAT_PATTERN.matcher(str);
		return matcher.find();
	}

	public static String format(Number number) {
		return FORMAT.format(number.doubleValue());
	}
}
