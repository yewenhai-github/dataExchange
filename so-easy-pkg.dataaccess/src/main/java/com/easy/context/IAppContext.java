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
public interface IAppContext {
	/**
	 * 得到指定路径的配置文件输入流
	 * 
	 * @param path
	 *            配置文件相对路径
	 * @return InputStream 配置文件输入流
	 */
	public InputStream getResourceAsStream(String path);

	public String getRealPath();

}
