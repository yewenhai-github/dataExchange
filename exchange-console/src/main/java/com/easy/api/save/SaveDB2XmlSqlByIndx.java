package com.easy.api.save;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveDB2XmlSqlByIndx")
public class SaveDB2XmlSqlByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = GetDataValue("DB2XmlSqlData", "INDX");  
		String Pindx = GetDataValue("DB2XmlSqlData", "P_INDX");
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("DB2XmlSqlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("DB2XmlSqlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}else{
			SaveToTable("DB2XmlSqlData", "P_INDX", Pindx);
		} 
		String xmlSql=GetDataValue("DB2XmlSqlData", "XML_SQL");
		String xmlSql1=GetDataValue("DB2XmlSqlData", "XML_SQL1");
		String xmlSql2=GetDataValue("DB2XmlSqlData", "XML_SQL2");
		String xmlSql3=GetDataValue("DB2XmlSqlData", "XML_SQL3");
		
		if (!SysUtility.isEmpty(xmlSql)) {
			xmlSql=xmlSql.replaceAll("<BR>","\r");
			SaveToTable("DB2XmlSqlData", "XML_SQL", xmlSql);
		}
		if (!SysUtility.isEmpty(xmlSql1)) {
			xmlSql1=xmlSql1.replaceAll("<BR>","\r");
			SaveToTable("DB2XmlSqlData", "XML_SQL1", xmlSql1);
		}	
		if (!SysUtility.isEmpty(xmlSql2)) {
			xmlSql2=xmlSql2.replaceAll("<BR>","\r");
			SaveToTable("DB2XmlSqlData", "XML_SQL2", xmlSql2);
		}	
		if (!SysUtility.isEmpty(xmlSql3)) {
			xmlSql3=xmlSql3.replaceAll("<BR>","\r");
			SaveToTable("DB2XmlSqlData", "XML_SQL3", xmlSql3);
		}  
		
		SaveToDB("DB2XmlSqlData", "exs_config_dbtoxml_SQL");
		ReturnMessage(true, "保存成功！","", TableToJSON("DB2XmlSqlData"));
	}
}