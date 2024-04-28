package com.easy.api.convert.mess;


import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.web.MainServlet;

@WebServlet("/GetMessFileLogByIndx")
public class GetMessFileLogByIndx extends MainServlet {
	
	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("INDX");
		String  sql  = "@SELECT * FROM exs_convert_log T   WHERE	T.INDX ='" + indx + "'";
		JSONObject jsonObj = GetReturnDatas(sql);
		JSONArray jsonArr = jsonObj.getJSONArray("rows");
		jsonObj.put("MessFileLogDATA", jsonArr);
		ReturnMessage(true, "", "", jsonObj.toString());	
	}

}