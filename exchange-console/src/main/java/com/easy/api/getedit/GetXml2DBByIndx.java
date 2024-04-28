package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetXml2DBByIndx")
public class GetXml2DBByIndx  extends MainServlet {
	private static final long serialVersionUID = 2704556519216096772L;

	public void DoCommand() throws Exception {
		InitFormData("Xml2DBData", SQLMap.getSelect("GetXml2DBByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
