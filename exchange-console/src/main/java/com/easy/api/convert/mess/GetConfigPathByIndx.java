package com.easy.api.convert.mess;


import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.web.MainServlet;


@WebServlet("/GetConfigPathByIndx")
public class GetConfigPathByIndx extends MainServlet {
	
	private static final long serialVersionUID = 8642196126064795851L;

	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("INDX");
		String  sql  = "@SELECT * FROM exs_convert_config_path  T   WHERE	T.INDX ='" + indx + "'";
		JSONObject jsonObj = GetReturnDatas(sql);
		JSONArray jsonArr = jsonObj.getJSONArray("rows");
		jsonObj.put("MESSDATA", jsonArr);
		ReturnMessage(true, "", "", jsonObj.toString());	
	}

}