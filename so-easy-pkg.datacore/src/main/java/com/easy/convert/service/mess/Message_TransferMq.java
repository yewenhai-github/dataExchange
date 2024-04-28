package com.easy.convert.service.mess;

import com.easy.app.interfaces.IGlobalService;
import com.easy.convert.service.util.MessTransUtil;
import com.easy.exception.LegendException;
import com.easy.web.MainServlet;

public class Message_TransferMq extends MainServlet implements IGlobalService{

	public Message_TransferMq(String param) {
		super();
	}
	
	public void DoCommand() throws LegendException, Exception {
		MessTransUtil.ZipMQToXml(getDataAccess());
	}
	
}
