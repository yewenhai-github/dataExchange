package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetQuoteConfigByIndx")
public class GetQuoteConfigByIndx extends MainServlet {
	private static final long serialVersionUID = 1L;

	public void DoCommand() throws Exception {
		InitFormData("XmlData", SQLMap.getSelect("GetQuoteConfigByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
