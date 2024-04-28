package com.easy.exception;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public final class ErrorMessage {
	
	private static final Pattern LEVEL_CODE_AND_DELIMITERE_PATTERN = Pattern.compile("[0-6]:");

	private final Map ERR_CODE_MAP = new HashMap();

	private static final ErrorMessage INSTANCE = new ErrorMessage();

	private ErrorMessage() {
		InputStream is = ErrorMessage.class.getResourceAsStream("ErrorCode.properties");

		Properties props = new Properties();
		try {
			props.load(is);
		} catch (Exception e) {
			LogUtil.printLog("ErrorCode.properties load error!" + e.getMessage(), LogUtil.DEBUG);
		} finally {
			closeInputStream(is);
		}
		convert(ERR_CODE_MAP, props, "utf-8");
	}

	private void convert(Map map, Properties props, String charSetName) {
		if (SysUtility.isEmpty(charSetName)) {
			map.putAll(props);
		} else {
			Set set = props.entrySet();
			for (Iterator it = set.iterator(); it.hasNext();) {
				Entry entry = (Entry) it.next();
				String key = convert((String) entry.getKey(), charSetName);
				String value = convert((String) entry.getValue(), charSetName);
				map.put(key, value);
			}
		}
	}

	private String convert(String str, String charSetName) {
		try {
			return new String(str.getBytes("iso8859-1"), charSetName);
		} catch (UnsupportedEncodingException e) {
			return SysUtility.EMPTY;
		}
	}

	public static ErrorMessage getInstance() {
		return INSTANCE;
	}

	private String getErrorMessage(int code) {
		return (String) ERR_CODE_MAP.get(String.valueOf(code));
	}

	public String getErrorMessage(int errorCode, String[] lstPattern) {
		String errorMessage = getErrorMessage(errorCode);

		if (errorMessage != null) {
			if (checkLevel(errorMessage)) {
				errorMessage = errorMessage.substring(2);
			}

			if (lstPattern != null) {
				for (int i = 0; i < lstPattern.length; i++) {
					if (lstPattern[i] != null)
						errorMessage = errorMessage.replaceAll("%" + (i + 1), lstPattern[i]);
				}
			}
		}

		return errorMessage;
	}
	
	public String getErrorMessageWithoutCheckLevel(int errorCode, String[] lstPattern) {
		String errorMessage = getErrorMessage(errorCode);

		if (errorMessage != null) {
			if (lstPattern != null) {
				for (int i = 0; i < lstPattern.length; i++) {
					if (lstPattern[i] != null)
						errorMessage = errorMessage.replaceAll("%" + (i + 1), lstPattern[i]);
				}
			}
		}

		return errorMessage;
	}

	public boolean checkLevel(String errorMessage) {

		if (errorMessage.length() >= 2) {
			String levelCodeAndDelimiter = errorMessage.substring(0, 2);
			if (LEVEL_CODE_AND_DELIMITERE_PATTERN.matcher(levelCodeAndDelimiter).matches()) {
				return true;
			}
		}
		return false;
	}

	public int getErrorLevel(int code) {
		String errorMessage = getErrorMessage(code);

		if (errorMessage != null) {
			if (checkLevel(errorMessage)) {
				return Integer.parseInt(errorMessage.substring(0, 1));
			}
		}
		return ErrorLevel.NONE;
	}
	
	public static void closeInputStream(InputStream is) {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (Exception e) {
        }
    }
}
