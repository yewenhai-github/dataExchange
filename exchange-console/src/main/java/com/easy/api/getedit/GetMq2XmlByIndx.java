package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/GetMq2XmlByIndx")
public class GetMq2XmlByIndx extends MainServlet {
	private static final long serialVersionUID = 2704556519216096772L;

	public void DoCommand() throws Exception {
		InitFormData("XmlData", SQLMap.getSelect("GetMq2XmlByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
