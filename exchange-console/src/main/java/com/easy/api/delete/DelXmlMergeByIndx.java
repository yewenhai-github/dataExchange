package com.easy.api.delete;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/DelXmlMergeByIndx")
public class DelXmlMergeByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		
			SaveToTable("XmlData", "IS_ENABLED", "0");
			SaveToTable("XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
			SaveToDB("XmlData", "exs_config_xmltomerge");
			ReturnMessage(true, "失效成功！" ,"", TableToJSON("XmlData"));
		
	}
}