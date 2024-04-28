package com.easy.app.eciq;



import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;


@WebServlet("/forms/getQuarantine")
public class getQuarantine extends MainServlet{
	private static final long serialVersionUID = 1L;

	public getQuarantine(){
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String action = getRequest().getParameter("action");
		if("getjson".equals(action)){
			InitFormData("TineData", SQLMap.getSelect("getQuarantineById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}else{
			InitFormData("TineData", SQLMap.getSelect("getQuarantine"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
	
		
	}
}
