package com.easy.api.save;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveDB2ComXmlByPIndx")
public class SaveDB2ComXmlByPIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = GetDataValue("DB2XmlComData", "INDX");
		String P_INDX= GetDataValue("DB2XmlComData", "P_INDX");
		if(SysUtility.isEmpty(P_INDX)){
			 P_INDX= GetDataValue("DB2XmlData", "INDX");
		}                         
		SaveToTable("DB2XmlComData","P_INDX",P_INDX);// 修改人
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("DB2XmlComData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("DB2XmlComData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		                           
		SaveToDB("DB2XmlComData", "exs_config_dbtoxml_SQL");
		ReturnMessage(true, "保存成功！","", TableToJSON("DB2XmlComData"));
		

	}
}