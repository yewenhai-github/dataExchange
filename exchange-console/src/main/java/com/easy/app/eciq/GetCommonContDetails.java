package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetCommonContDetails")
public class GetCommonContDetails  extends MainServlet{  
	private static final long serialVersionUID = 3594076222956321552L; 
	public GetCommonContDetails()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		String action=getRequest().getParameter("action");
		String CONT_ID=getRequest().getParameter("CONT_ID");
		String DECL_NO=getRequest().getParameter("DECL_NO");
		String CONT_DT_ID=getRequest().getParameter("CONT_DT_ID");
		
		
		if("getContDetailsJson".equals(action)){
			InitFormData("contDetail", SQLMap.getSelect("GetCommonContDetailsByIndx")); 

			ReturnMessage(true, "", "", getFormDatas().toString());

		}
		
		if("getContDetailsList".equals(action)){
			if(SysUtility.isEmpty(CONT_ID))
				AddToSearchTable("CONT_ID", "0");
			else
				AddToSearchTable("CONT_ID", CONT_ID);
			AddToSearchTable("DECL_NO", DECL_NO);
			ReturnWriter(GetReturnDatas("GetImportContDetailsList").toString());
		}
		
	}
}
