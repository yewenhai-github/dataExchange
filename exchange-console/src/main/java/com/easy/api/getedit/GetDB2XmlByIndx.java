package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetDB2XmlByIndx")
public class GetDB2XmlByIndx extends MainServlet {
	public void DoCommand() throws Exception {
		InitFormData("DB2XmlData", SQLMap.getSelect("GetDB2XmlByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
