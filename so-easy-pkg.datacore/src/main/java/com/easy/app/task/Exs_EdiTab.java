package com.easy.app.task;

import com.easy.access.IDataAccess;
import com.easy.access.MySqlDataAccess;
import com.easy.access.OraDataAccess;
import com.easy.access.SqlDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.utility.ExsUtility;
import com.easy.constants.Constants;
import com.easy.entity.ServicesBean;
import com.easy.session.SessionManager;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Exs_EdiTab extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_EdiTab(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		if(SysUtility.quartzLimit()){
			return;
		}
		
		IDataAccess DataAccess = SessionManager.getDataAccess();
		
		ServicesBean bean = new ServicesBean();
		bean.setServiceMode(ExsConstants.DBToXml);
		ExsUtility.CToXml(bean, DataAccess);
	}
	
}
