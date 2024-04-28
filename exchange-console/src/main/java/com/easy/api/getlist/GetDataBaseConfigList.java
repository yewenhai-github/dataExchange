package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.constants.ExsConstants;
import com.easy.app.utility.ExpUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetDataBaseConfigList")
public class GetDataBaseConfigList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		AddToSearchTable("CATEGORY", ExsConstants.DataBase);
		JSONObject rows = GetReturnDatasAllDB("GetQuoteConfigList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}