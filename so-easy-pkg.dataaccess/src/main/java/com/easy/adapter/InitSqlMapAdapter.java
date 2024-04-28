package com.easy.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Level;

import com.easy.query.SQLMap;
import com.easy.utility.LogUtil;

public class InitSqlMapAdapter extends GenericServlet{
	private static final long serialVersionUID = -8505611749348389713L;

	public void init() throws ServletException {
		super.init();
		initSqlMap(new ArrayList<String>());
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
