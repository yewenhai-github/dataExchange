package com.easy.security;

import java.security.MessageDigest;

import com.easy.exception.LegendException;

public class MD5Utility {
	/**
	 * MD5加密
	 * **/
	public static String encrypt(String password) throws LegendException {
		return new Md5().compute(password);
	}
	
	private static class Md5 {
		private MessageDigest md5Code = null;

		private void init() throws LegendException {
			try {
				md5Code = MessageDigest.getInstance("MD5");
			} catch (Exception e) {
				throw LegendException.getLegendException("MD5加密失败");
			}
		}

		public String compute(String inStr) throws LegendException {
			init();
			char[] charArray = inStr.toCharArray();
			byte[] byteArray = new byte[charArray.length];
			for (int i = 0; i < charArray.length; i++) {
				byteArray[i] = (byte) charArray[i];
			}

			// 加密
			byte[] md5Bytes = md5Code.digest(byteArray);
			StringBuffer hexValue = new StringBuffer();

			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		}
	}
}
