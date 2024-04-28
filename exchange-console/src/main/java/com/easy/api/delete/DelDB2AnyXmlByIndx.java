package com.easy.api.delete;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DelDB2AnyXmlByIndx")
public class DelDB2AnyXmlByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String isEnabled = (String)GetCommandData("XmlData", "IS_ENABLED");
		SaveToTable("XmlData", "IS_ENABLED", "0");
		SaveToTable("XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
		SaveToTable("XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		SaveToDB("XmlData", "exs_config_dbtoxml");
		ReturnMessage(true, "失效成功！" ,"", TableToJSON("XmlData"));
		
	}
}