package com.easy.api.delete;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DelComDB2XmlByIndx")
public class DelComDB2XmlByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String isEnabled = (String)GetCommandData("DB2XmlData", "IS_ENABLED");
		SaveToTable("DB2XmlData", "IS_ENABLED", "0");
		SaveToTable("DB2XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
		SaveToTable("DB2XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		SaveToDB("DB2XmlData", "exs_config_dbtoxml");
		ReturnMessage(true, "失效成功！" ,"", TableToJSON("DB2XmlData"));
		
	}
}