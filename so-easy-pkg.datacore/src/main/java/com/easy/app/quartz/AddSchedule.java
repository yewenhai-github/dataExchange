package com.easy.app.quartz;

import javax.servlet.annotation.WebServlet;

import com.easy.quartz.QuartzContext;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/AddSchedule")
public class AddSchedule extends MainServlet {
	private static final long serialVersionUID = 1L;

	public AddSchedule() {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		final String JobName = (String)GetEnvDatas("NAME");
		if(SysUtility.isEmpty(JobName)){
			ReturnMessage(false, "NAME参数不能为空！");
		}
		QuartzContext.AddCurrentSchedule(JobName);
		ReturnMessage(true, "定时器："+JobName+"装载成功！");
	}
	
	
 }
