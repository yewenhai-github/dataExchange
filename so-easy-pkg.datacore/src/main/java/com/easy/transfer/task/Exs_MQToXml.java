package com.easy.transfer.task;

import com.easy.app.interfaces.IGlobalService;
import com.easy.transfer.utility.TransferUtility;
import com.easy.web.MainServlet;

public class Exs_MQToXml extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_MQToXml(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		TransferUtility.MQToXml(getDataAccess());
	}
	
}
