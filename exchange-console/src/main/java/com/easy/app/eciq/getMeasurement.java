package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/forms/getMeasurement")
public class getMeasurement extends MainServlet{
	private static final long serialVersionUID = 1L;

	public getMeasurement(){
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String action = getRequest().getParameter("action");
		String TINE_NO=getRequest().getParameter("TINE_NO");
		if("list".equals(action)){
			 if(!"".equals(TINE_NO)){
				 AddToSearchTable("TINE_NO", TINE_NO);
			     ReturnWriter(GetReturnDatas("getMeasurementList").toString()); 
			 }
			
		}else if("getjson".equals(action)){
			InitFormData("measData", SQLMap.getSelect("getMeasurementById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		
		}
	}
}
