package com.easy.api.delete;

import javax.servlet.annotation.WebServlet;

import com.easy.access.Datas;
import com.easy.web.MainServlet;

@WebServlet("/DelQuoteConfigByIndx")
public class DelQuoteConfigByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception {
		Datas datas = getDataAccess().GetTableDatas("TEMP", "select * from exs_quote_config where indx = ?", GetDataValue("XmlData", "INDX"));
		if("1".equals(datas.GetTableValue("TEMP", "AUDIT_FLAG"))) {
			ReturnMessage(false, "已审核数据不允许删除，请您联系管理员!");
			return;
		}
		
		try {
			DeleteDB("XmlData", "exs_quote_config");
			ReturnMessage(true, "删除成功！");
		} catch (Exception e) {
			ReturnMessage(true, "删除失败！","",TableToJSON("XmlData"));
		}
		
		
	}
}