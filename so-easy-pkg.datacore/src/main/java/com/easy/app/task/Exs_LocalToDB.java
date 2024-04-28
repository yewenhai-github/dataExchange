package com.easy.app.task;

import org.apache.log4j.Level;

import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.utility.ExsUtility;
import com.easy.entity.ServicesBean;
import com.easy.session.SessionManager;
import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Exs_LocalToDB extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_LocalToDB(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		if(SysUtility.quartzLimit()){
			return;
		}
		
		IDataAccess DataAccess = SessionManager.getDataAccess();
		if(SysUtility.isEmpty(DataAccess)) {
			LogUtil.printLog("DataAccess为空，无法初始化！", Level.ERROR);
		}
		
		ServicesBean bean = new ServicesBean();
		bean.setServiceMode(ExsConstants.XmlToDB);
		ExsUtility.XmlToC(bean, DataAccess);
	}
	
}
