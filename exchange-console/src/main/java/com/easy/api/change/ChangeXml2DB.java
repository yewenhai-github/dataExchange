package com.easy.api.change;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/ChangeXml2DB")
public class ChangeXml2DB extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
//		DeleteDB("Xml2DBData", "exs_config_xmltomq");
//		String commandname = (String)GetCommandData("Xml2DBData", "commandname");
		String isEnabled = (String)GetCommandData("Xml2DBData", "IS_ENABLED");
		if("1".equals(isEnabled)){
			SaveToTable("Xml2DBData", "IS_ENABLED", "0");
			SaveToDB("Xml2DBData", "EXS_CONFIG_XMLTODB");
			ReturnMessage(true, "失效成功！","", TableToJSON("Xml2DBData"));
		}else{
			SaveToTable("Xml2DBData", "IS_ENABLED", "1");
			SaveToDB("Xml2DBData", "EXS_CONFIG_XMLTODB");
			ReturnMessage(true, "恢复成功！","", TableToJSON("Xml2DBData"));
		}
	}
}