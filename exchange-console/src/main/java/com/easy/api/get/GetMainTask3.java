package com.easy.api.get;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMainTask3")
public class GetMainTask3 extends MainServlet {
	private static final long serialVersionUID = 7708330752325002855L;

	public GetMainTask3() {
		CheckLogin = false;
	}
	
	public void DoCommand() throws Exception{
		JSONObject rows = GetReturnDatasAllDB("GetMainTask3");
		JSONObject rt = (JSONObject)((JSONArray)rows.get("rows")).get(0);
		ExpUtility.setRowsDefault(rt);
		ReturnWriter(rt.toString());
	}

}