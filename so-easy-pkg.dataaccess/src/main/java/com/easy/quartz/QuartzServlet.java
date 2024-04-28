package com.easy.quartz;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public class QuartzServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 初始化Servlet
	 */
	public void init() throws ServletException {
		super.init();
		QuartzContext.init();
	}

	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException,
			IOException {
	}

	public String getServletInfo() {
		return null;
	}

	public void destroy() {
		super.destroy();
		QuartzContext.destroy();
	}
}
