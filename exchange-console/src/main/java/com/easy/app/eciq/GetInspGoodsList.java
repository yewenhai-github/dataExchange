package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspGoodsList")
public class GetInspGoodsList extends MainServlet {

	private static final long serialVersionUID = 1016011913982790116L;
	
	public GetInspGoodsList(){
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception {
		String allId=getRequest().getParameter("allId");
		String DECL_NO = (String)getEnvDatas().get("DECL_NO");
		if(SysUtility.isEmpty(DECL_NO)&&SysUtility.isEmpty(allId)){
			return;
		}
		if(SysUtility.isNotEmpty(allId)){
			String [] searchIds=allId.substring(0,allId.length()-1).split(",");
			AddToSearchTable("PINDX", searchIds);
			ReturnWriter(GetReturnDatas("GetDistriGoodsByPindxs").toString());
		}else{
			this.AddToSearchTable("DECL_NO", DECL_NO);
			ReturnWriter(GetReturnDatas("GetInspGoodsList").toString());
		}
		
	}


}
