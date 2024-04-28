package com.easy.http;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class HttpConfig {

	/**
	 * 单个request，连接超时时间，由bean factory设置，缺省为8秒钟
	 */
	public static int httpRequestDefaultConnectionTimeout = 8 * 1000;

	/**
	 * 回应超时时间, 由bean factory设置，缺省为30秒钟
	 */
	public static int httpDefaultSoTimeout = 30000;

	/**
	 * 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒
	 */
	public static long httpDefaultConnectionManagerTimeout = 20 * 1000;

	/**
	 * 单个request连接闲置连接超时时间, 由bean factory设置，缺省为60秒钟
	 */
	public static int httpDefaultIdleConnTimeout = 60000;

	/**
	 * 允许最大连接主机数,默认是2
	 */
	public static int httpDefaultMaxConnPerHost = 30;

	/**
	 * 允许连接的最大数
	 */
	public static int httpDefaultMaxTotalConn = 80;
}
