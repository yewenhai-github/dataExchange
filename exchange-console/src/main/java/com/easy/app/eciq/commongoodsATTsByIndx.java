package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/forms/commongoodsATTsByIndx")
public class commongoodsATTsByIndx  extends MainServlet {

	private static final long serialVersionUID = -7722225571192321843L;
	public commongoodsATTsByIndx()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{ 
		String ATT_ID = getRequest().getParameter("ATT_ID");
		
		InitFormData("LimitData", SQLMap.getSelect("commongoodsATTsByIndx"));
		
		ReturnMessage(true, "", "", getFormDatas().toString());
		
	}
}
