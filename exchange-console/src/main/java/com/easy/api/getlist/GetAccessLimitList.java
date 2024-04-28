package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetAccessLimitList")
public class GetAccessLimitList extends MainServlet {
	private static final long serialVersionUID = 4319536019822293819L;

	public void DoCommand() throws Exception{
		JSONObject rows = GetReturnDatasAllDB("GetAccessLimitList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}