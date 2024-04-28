package com.easy.convert.service.mess;

import com.easy.access.IDataAccess;
import com.easy.app.constants.ExsConstants;
import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.service.util.MessUtility;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Message_TransferForLocal extends MainServlet implements IGlobalService{

	private static final String ThreadCount = SysUtility.isEmpty(SysUtility.GetSetting("System", "MAXTHREADCOUNT"))?"":SysUtility.GetSetting("System", "MAXTHREADCOUNT");
	public Message_TransferForLocal(String Param) {
		super();
	}
	/**
	 * 本地报文转换
	 */
	private static final long serialVersionUID = 8269406944851803439L;

	@Override
	public void DoCommand() throws LegendException, Exception {
		IDataAccess dataAccess = getDataAccess();
		ServicesBean bean = new ServicesBean();
//		bean.setThreadCount(ThreadCount);
		bean.setServiceMode(ExsConstants.Local);
		MessUtility.MessToxml(bean, dataAccess);
	}

	
	
}
