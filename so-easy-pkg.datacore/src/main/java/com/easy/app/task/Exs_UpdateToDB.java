package com.easy.app.task;

import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.interfaces.IGlobalService;
import com.easy.app.utility.ExsUtility;
import com.easy.entity.ServicesBean;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Exs_UpdateToDB extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_UpdateToDB(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		IDataAccess DataAccess = getDataAccess();
		if(SysUtility.quartzLimit()){
			return;
		}
		ServicesBean bean = new ServicesBean();
		bean.setServiceMode(ExsConstants.UpdateDBToDB);
		ExsUtility.DBToDB(bean, DataAccess);
	}
	
}
