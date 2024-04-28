package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetExsSenderByIndx")
public class GetExsSenderByIndx extends MainServlet {
	public void DoCommand() throws Exception {
		InitFormData("XmlData", SQLMap.getSelect("GetExsSenderByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
