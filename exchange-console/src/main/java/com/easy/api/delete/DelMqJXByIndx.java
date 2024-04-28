package com.easy.api.delete;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DelMqJXByIndx")
public class DelMqJXByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
			SaveToTable("XmlData", "IS_ENABLED", "0");
			SaveToDB("XmlData", "exs_config_mqtoxml");
			ReturnMessage(true, "失效成功！" ,"", TableToJSON("XmlData"));
	}
}