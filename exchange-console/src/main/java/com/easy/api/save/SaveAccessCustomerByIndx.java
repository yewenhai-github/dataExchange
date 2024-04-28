package com.easy.api.save;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SaveAccessCustomerByIndx")
public class SaveAccessCustomerByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = GetDataValue("XmlData", "INDX"); 
		SaveToTable("XmlData", "IS_ENABLED", "1");
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		SaveToDB("XmlData", "EXS_ACCESS_CUSTOMER");
		ReturnMessage(true, "保存成功！" ,"", TableToJSON("XmlData"));
		 

	}
}
