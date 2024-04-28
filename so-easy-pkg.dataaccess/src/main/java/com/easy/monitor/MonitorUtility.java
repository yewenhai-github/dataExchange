package com.easy.monitor;

import java.net.HttpURLConnection;
import java.net.URL;

public class MonitorUtility {
	
	/**@apiNote 连接3次，若是3次都连不上，就抛出异常
	 * @param connurl
	 * */
	public static int geturlstate(String connurl) {
		int state = -1;
		
		// 判断连接是否存在
		if (connurl == null || connurl.length() == 0) {
			throw new RuntimeException("url为空");
		}
		try {
			URL url = new URL(connurl);// 创建url对象
			HttpURLConnection con = (HttpURLConnection) url.openConnection();// 打开url连接
			state = con.getResponseCode();
			return state;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
}
