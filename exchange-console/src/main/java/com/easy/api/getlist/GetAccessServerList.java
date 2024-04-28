package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetAccessServerList")
public class GetAccessServerList extends MainServlet {
	private static final long serialVersionUID = -796545488075821680L;

	public void DoCommand() throws Exception{
		JSONObject rows = GetReturnDatasAllDB("GetAccessServerList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}