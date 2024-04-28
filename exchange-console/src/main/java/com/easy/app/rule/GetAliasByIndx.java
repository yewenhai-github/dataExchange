package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
@WebServlet("/GetAliasByIndx")

public class GetAliasByIndx extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;
	
	public void DoCommand() throws Exception{
		String indx = (String)getEnvDatas().get("indx");
		if(SysUtility.isEmpty(indx)){
			return;
		}
		String SQL = "Select *  from RULE_T_ALIAS Where Indx ="+indx;
		AddToSearchTable("INDX", indx);
		ReturnMessage(true, "", "", GetReturnDatas("@"+SQL,"Role").toString());
		//ReturnWriter(GetReturnDatas("@"+SQL,"Role").toString());
	}
}

