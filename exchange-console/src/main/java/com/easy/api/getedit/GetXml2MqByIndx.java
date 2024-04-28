package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetXml2MqByIndx")
public class GetXml2MqByIndx extends MainServlet {
	public void DoCommand() throws Exception {
		InitFormData("Xml2MqData", SQLMap.getSelect("GetXml2MqByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
