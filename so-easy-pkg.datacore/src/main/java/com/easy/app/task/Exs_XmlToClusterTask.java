package com.easy.app.task;

import com.easy.access.IDataAccess;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.utility.ExsUtility;
import com.easy.entity.ServicesBean;
import com.easy.web.MainServlet;

public class Exs_XmlToClusterTask extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_XmlToClusterTask(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		IDataAccess DataAccess = getDataAccess();
		ServicesBean bean = new ServicesBean();
//		ExsUtility.XmlToClusterTask(bean, DataAccess);
	}
	
}
