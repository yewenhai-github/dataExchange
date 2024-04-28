package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class ProtocolConstant {
	
	public static final String DATE_TIME_FORMAT = "yyyyMMddHHmmss";

	public enum ProtocolType {
		HTTP("http"), HTTPS("https");
		private final String value;

		public String getValue() {
			return value;
		}

		ProtocolType(String value) {
			this.value = value;
		}
	}

	public enum Charset {
		UTF_8("UTF-8"), GBK("GBK"), DEFAULT("UTF-8");
		private final String value;

		public String getValue() {
			return value;
		}

		Charset(String value) {
			this.value = value;
		}
	}

	public enum DataType {
		INPUTSTREAM("InputStream"),STRING("string"), BYTES("bytes"), DEFAULT("string");
		private final String value;

		public String getValue() {
			return value;
		}

		DataType(String value) {
			this.value = value;
		}
	}

	public enum MethodType {
		GET("GET"), POST("POST"), DEFAULT("POST");
		private final String value;

		public String getValue() {
			return value;
		}

		MethodType(String value) {
			this.value = value;
		}
	}
}
