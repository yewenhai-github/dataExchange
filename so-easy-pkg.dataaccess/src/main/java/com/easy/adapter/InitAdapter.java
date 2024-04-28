package com.easy.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

import com.easy.bizconfig.BizConfigFactory;
import com.easy.context.AppContext;
import com.easy.context.ServletContextImpl;
import com.easy.query.SQLMap;
import com.easy.session.SessionKeyType;
import com.easy.session.SessionManager;
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
public class InitAdapter extends GenericServlet{
	private static final long serialVersionUID = -8505611749348389713L;

	public void init() throws ServletException {
		super.init();
		initContext();//初始化上下问
		//initLog4j();//初始化log4j日志
		initSqlMap(new ArrayList<String>());
		
		if(SysUtility.IsOracleDB() && !SysUtility.getDBPoolClose()){
			initBizConfig();//初始化分参
		}
	}
	
	public void initContext(){
		AppContext.init(new ServletContextImpl(getServletContext()));
		if(SysUtility.isNotEmpty(getServletContext().getContextPath())) {
			AppContext.setContextPath(getServletContext().getContextPath().substring(1));
		}
		AppContext.setAbsolutePath(getServletContext().getRealPath(""));;
		if(SysUtility.isNotEmpty(getServletContext().getContextPath())){
			SessionManager.setAttribute(SessionKeyType.SESSION_APP_NAME, getServletContext().getContextPath().substring(1));
		}
	}
	
	public void initLog4j() {
		try {
			PropertyConfigurator.configure(java.net.URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("config/log4j.properties").getPath(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void initBizConfig() {
		BizConfigFactory.reload();
	}
	
    public void initSqlMap(List<String> viewList){
    	String view = "";
    	try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			for (int i = 0; i < viewList.size(); i++) {
				view = viewList.get(i);
				SQLMap.add(cl.getResourceAsStream(view));
				LogUtil.printLog("初始化SqlMap成功,文件:"+view+"", Level.INFO);
			}
		} catch (Exception e) {
			LogUtil.printLog("初始化SqlMap出错,文件:"+view+"不存在或内存出错！"+e.getMessage(), Level.ERROR);
		}
	}

	public void destroy() {
		super.destroy();
	}

	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
	}
	
}
