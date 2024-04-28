package com.easy.app.eciq;



import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;


@WebServlet("/forms/getcheckEdit")
public class getcheckEdit extends MainServlet{
	private static final long serialVersionUID = 1L;

	public getcheckEdit(){
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String INDX= getRequest().getParameter("INDX");
		String CONT_ID=getRequest().getParameter("CONT_ID");
		if(!SysUtility.isEmpty(CONT_ID)){
			InitFormData("CheckDATA", SQLMap.getSelect("getCheckEditCONTID"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
		if(!SysUtility.isEmpty(INDX)){
			InitFormData("CheckDATA", SQLMap.getSelect("getCheckEditById"));
			ReturnMessage(true, "", "", getFormDatas().toString());
		}
	}
}
