package com.easy.api.task;

import com.easy.api.utility.QuartzApiUtility;
import com.easy.app.constants.ExsConstants;
import com.easy.app.interfaces.IGlobalService;
import com.easy.entity.ServicesBean;
import com.easy.web.MainServlet;

public class Exs_ApiPollingToInvoke extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_ApiPollingToInvoke(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		ServicesBean bean = new ServicesBean();
		bean.setServiceMode(ExsConstants.ApiPollingToInvoke);
		QuartzApiUtility.ApiToC(bean, getDataAccess());
	}
}