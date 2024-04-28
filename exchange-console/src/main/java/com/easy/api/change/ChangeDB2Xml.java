package com.easy.api.change;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/ChangeDB2Xml")
public class ChangeDB2Xml extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String isEnabled = (String)GetCommandData("DB2XmlData", "IS_ENABLED");
		if("1".equals(isEnabled)){
			SaveToTable("DB2XmlData", "IS_ENABLED", "0");
			SaveToDB("DB2XmlData", "exs_config_dbtoxml");
			ReturnMessage(true, "失效成功！" ,"", TableToJSON("DB2XmlData"));
		}else{
			SaveToTable("DB2XmlData", "IS_ENABLED", "1");
			SaveToDB("DB2XmlData", "exs_config_dbtoxml");
			ReturnMessage(true, "恢复成功！" ,"", TableToJSON("DB2XmlData"));
		}
	}
}