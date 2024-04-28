package com.easy.context;

import java.io.InputStream;

/**
 * so-easy private
 * 
 * @author yewh 2015-10-20
 * 
 * @version 7.0.0
 * 
 */
/** 
 * Description: APP上下文对象
 */
public class AppContext {
	private static IAppContext appContext = null;
	private static String contextPath = "";
	private static String contextPort = "";
	private static String servletPath = "";
	private static String absolutePath = "";
	
	/**
	 * APP上下文环境初始化
	 * @param iAppContext
	 *            IAppContext接口
	 * @param propFileName
	 *            配置文件相对路径
	 */
	public static void init(IAppContext iAppContext) {
		appContext = iAppContext;
	}

	/**
	 * 得到指定路径的配置文件输入流
	 * 
	 * @param path
	 *            配置文件相对路径
	 * @return InputStream 配置文件输入流
	 */
	public static InputStream getResourceAsStream(String path) {
		return appContext.getResourceAsStream(path);
	}

	public static String getRealPath() {
		return appContext.getRealPath();
	}
	
	public static void setContextPath(String ContextPath) {
		contextPath = ContextPath;
	}
	
	public static String getContextPath(){
		return contextPath;
	}

	public static String getContextPort() {
		return contextPort;
	}

	public static void setContextPort(String contextPort) {
		AppContext.contextPort = contextPort;
	}

	public static String getServletPath() {
		return servletPath;
	}

	public static void setServletPath(String servletPath) {
		AppContext.servletPath = servletPath;
	}

	public static String getAbsolutePath() {
		return absolutePath;
	}

	public static void setAbsolutePath(String absolutePath) {
		AppContext.absolutePath = absolutePath;
	}
}
