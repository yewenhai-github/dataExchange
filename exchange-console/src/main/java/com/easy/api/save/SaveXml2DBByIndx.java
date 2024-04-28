package com.easy.api.save;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveXml2DBByIndx")
public class SaveXml2DBByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = GetDataValue("Xml2DBData", "INDX");  
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("Xml2DBData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("Xml2DBData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		SaveToDB("Xml2DBData", "EXS_CONFIG_XMLTODB");
		ReturnMessage(true, "保存成功！","", TableToJSON("Xml2DBData"));
		

	}
}