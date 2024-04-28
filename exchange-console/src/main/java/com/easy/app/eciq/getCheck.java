package com.easy.app.eciq;



import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/getCheck")
public class getCheck extends MainServlet{
	private static final long serialVersionUID = 1L;

	public getCheck(){
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String INDX= getRequest().getParameter("INDX");
		String CONT_ID=getRequest().getParameter("CONT_ID");
		if(!SysUtility.isEmpty(CONT_ID)){
			InitFormData("CheckDATA", SQLMap.getSelect("getCheckByCONTID"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		if(!SysUtility.isEmpty(INDX)){
			InitFormData("CheckDATA", SQLMap.getSelect("getCheckById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
	}
}
