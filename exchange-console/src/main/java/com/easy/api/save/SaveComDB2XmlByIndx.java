package com.easy.api.save;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/SaveComDB2XmlByIndx")
public class SaveComDB2XmlByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = (String) getEnvDatas().get("INDX");
		if (!SysUtility.isEmpty(indx)) {
			SaveToTable("DB2XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("DB2XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}                    
		SaveToDB("DB2XmlData", "exs_config_dbtoxml");
		ReturnMessage(true, "保存成功！" ,"", TableToJSON("DB2XmlData"));
		

	}
}
