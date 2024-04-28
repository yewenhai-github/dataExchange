package com.easy.exception;

import org.apache.log4j.Level;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class ErrorLevel {
	public static final int NONE = 0;

	public static final int DEBUG = 1;

	public static final int INFO = 2;

	public static final int WARN = 3;

	public static final int ERROR = 4;

	public static final int ALARM = 5;

	public static Level parseLevle(int levelCode) {
		Level level = null;
		switch (levelCode) {
		case NONE:
		case DEBUG:
			level = Level.DEBUG;
			break;
		case INFO:
			level = Level.INFO;
			break;
		case WARN:
			level = Level.WARN;
			break;
		case ERROR:
			level = Level.ERROR;
			break;
		case ALARM:
			level = Level.FATAL;
			break;
		default:
			level = Level.DEBUG;
			break;
		}
		return level;
	}
}
