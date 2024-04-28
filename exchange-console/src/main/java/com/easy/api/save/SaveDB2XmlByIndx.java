package com.easy.api.save;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveDB2XmlByIndx")
public class SaveDB2XmlByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = GetDataValue("DB2XmlData", "INDX");  
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("DB2XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("DB2XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		SaveToDB("DB2XmlData", "exs_config_dbtoxml");
		ReturnMessage(true, "保存成功！" ,"", TableToJSON("DB2XmlData"));
	}
}