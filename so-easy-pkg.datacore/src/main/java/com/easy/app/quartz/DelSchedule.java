package com.easy.app.quartz;

import javax.servlet.annotation.WebServlet;

import com.easy.quartz.QuartzContext;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DelSchedule")
public class DelSchedule extends MainServlet {
	private static final long serialVersionUID = 1L;

	public DelSchedule() {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		final String name = (String)GetEnvDatas("NAME");
		if(SysUtility.isEmpty(name)){
			ReturnMessage(false, "NAME参数不能为空！");
		}
		QuartzContext.DelCurrentSchedule(name);
		ReturnMessage(true, "定时器："+name+"卸载成功！");
		
	}

 }
