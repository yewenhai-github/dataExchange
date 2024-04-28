package com.easy.api.delete;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DelMqLYByIndx")
public class DelMqLYByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		/*DeleteDB("XmlData", "exs_config_xmltomq");
		ReturnMessage(true, "删除成功！","", "");*/
		String isEnabled = (String)GetCommandData("XmlData", "IS_ENABLED");
		SaveToTable("XmlData", "IS_ENABLED", "0");
		SaveToTable("XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
		SaveToTable("XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		SaveToDB("XmlData", "exs_config_xmltomq");
		ReturnMessage(true, "失效成功！" ,"", TableToJSON("XmlData"));
		
	}
}