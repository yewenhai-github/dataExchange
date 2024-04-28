package com.easy.api.save;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/SaveDB2AnyXmlByIndx")
public class SaveDB2AnyXmlByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		String indx = (String) getEnvDatas().get("INDX");
		if (!SysUtility.isEmpty(indx)) {
			Datas datas = getDataAccess().GetTableDatas("TEMP", "select * from exs_config_dbtoxml where indx = ?", indx);
			if("1".equals(datas.GetTableValue("TEMP", "AUDIT_FLAG"))) {
				ReturnMessage(false, "已审核数据不允许修改，请您联系管理员!");
				return;
			}
			SaveToTable("XmlData", "MODIFYOR", SysUtility.getCurrentOrgId());// 修改人
			SaveToTable("XmlData", "MODIFY_TIME", SysUtility.getSysDate());// 修改时间
		}
		SaveToDB("XmlData", "exs_config_dbtoxml");
		ReturnMessage(true, "保存成功！" ,"", TableToJSON("XmlData"));
		

	}
}
