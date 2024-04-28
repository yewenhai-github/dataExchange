package com.easy.convert.service.mess;

import com.easy.access.IDataAccess;
import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.constants.ExsConstants;
import com.easy.convert.service.util.MessUtility;
import com.easy.entity.ServicesBean;
import com.easy.exception.LegendException;
import com.easy.web.MainServlet;

public class Message_TransferOracle extends MainServlet implements IGlobalService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1458846663078180820L;
	
	public Message_TransferOracle(String Param) {
		super();
	}
	@Override
	public void DoCommand() throws LegendException, Exception {
		IDataAccess DataAccess = getDataAccess();
		ServicesBean bean = new ServicesBean();
		bean.setServiceMode(ExsConstants.Oracle);
		MessUtility.MessToxml(bean, DataAccess);
	}

	
}
