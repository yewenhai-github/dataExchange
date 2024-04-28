package com.easy.context;

import java.io.InputStream;

import javax.servlet.ServletContext;

/**
 * so-easy private
 * 
 * @author yewh 2015-10-20
 * 
 * @version 7.0.0
 * 
 */
/**
 * <p>
 * Description: Servlet环境下APP上下文环境实现
 * </p>
 */
public class ServletContextImpl implements IAppContext {
	ServletContext servletContext = null;

	/**
	 * 构造函数
	 * 
	 * @param servletContext
	 *            servletContext实例
	 */
	public ServletContextImpl(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * 得到指定路径的配置文件输入流
	 * 
	 * @param path
	 *            配置文件相对路径
	 * @return InputStream 配置文件输入流
	 */
	public InputStream getResourceAsStream(String path) {
		return servletContext.getResourceAsStream(path);
	}

	public String getRealPath() {
		return servletContext.getRealPath("");
	}

}
