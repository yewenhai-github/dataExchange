package com.easy.api.save;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveDB2AnyXmlByPIndx")
public class SaveDB2AnyXmlByPIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = GetDataValue("DB2ANYDATA", "INDX");
		String P_INDX= GetDataValue("DB2ANYDATA", "P_INDX");
		if(SysUtility.isEmpty(P_INDX)){
			 P_INDX= GetDataValue("XmlData", "INDX");
		}                         
		SaveToTable("DB2ANYDATA","P_INDX",P_INDX);// 修改人
		if (!SysUtility.isEmpty(indx)) {
			Datas datas = getDataAccess().GetTableDatas("TEMP", "select * from exs_config_dbtoxml where indx = ?", P_INDX);
			if("1".equals(datas.GetTableValue("TEMP", "AUDIT_FLAG"))) {
				ReturnMessage(false, "已审核数据不允许修改，请您联系管理员!");
				return;
			}
			SaveToTable("DB2ANYDATA", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("DB2ANYDATA", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		
		SaveToDB("DB2ANYDATA", "exs_config_dbtoxml_SQL");
		ReturnMessage(true, "保存成功！","", TableToJSON("DB2ANYDATA"));
		

	}
}