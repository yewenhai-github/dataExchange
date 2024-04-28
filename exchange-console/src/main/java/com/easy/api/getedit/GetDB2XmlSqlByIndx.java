package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetDB2XmlSqlByIndx")
public class GetDB2XmlSqlByIndx extends MainServlet {
	private static final long serialVersionUID = 2704556519216096772L;

	public void DoCommand() throws Exception {
		InitFormData("DB2XmlSqlData", SQLMap.getSelect("GetDB2XmlSqlByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}