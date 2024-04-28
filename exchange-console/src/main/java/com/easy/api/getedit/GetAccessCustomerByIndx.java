package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetAccessCustomerByIndx")
public class GetAccessCustomerByIndx extends MainServlet {
	public void DoCommand() throws Exception {
		InitFormData("XmlData", SQLMap.getSelect("GetAccessCustomerByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
