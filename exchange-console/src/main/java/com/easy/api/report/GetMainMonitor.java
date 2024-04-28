package com.easy.api.report;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMainMonitor")
public class GetMainMonitor extends MainServlet {
	private static final long serialVersionUID = 7708330752325002855L;

	public GetMainMonitor() {
		CheckLogin = false;
	}
	
	public void DoCommand() throws Exception{
		JSONObject rows = GetReturnDatasAllDB("GetMainMonitor");
		ReturnWriter(rows.toString());
	}

}