package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetQuartzConfigByIndx")
public class GetQuartzConfigByIndx extends MainServlet {
	public void DoCommand() throws Exception {
		InitFormData("XmlData", SQLMap.getSelect("GetQuartzConfigByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
