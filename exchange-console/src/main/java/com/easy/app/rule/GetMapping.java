package com.easy.app.rule;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMapping")

public class GetMapping extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String Indx="";
		if((String)getEnvDatas().get("Indx")!=null)
			Indx=(String)getEnvDatas().get("Indx");
		String SQL="Select * from RULE_B_MAPPING Where Indx = '" + Indx + "' ";
		
		ReturnMessage(true, "操作成功", "", GetReturnDatas("@"+SQL,"mapData").toString());
		}

}
