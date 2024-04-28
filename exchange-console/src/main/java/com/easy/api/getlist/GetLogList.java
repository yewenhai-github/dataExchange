package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetLogList")
public class GetLogList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String type = (String) GetEnvDatas("type");
		if("DataQuality".equals(type)){
			AddToSearchTable("MSG_STATUS","0");
		}
		JSONObject rows = GetReturnDatasAllDB("GetLogList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}