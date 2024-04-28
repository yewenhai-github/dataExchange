package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetElecDeclList")
public class GetElecDeclList  extends MainServlet{  
	private static final long serialVersionUID = 3594076222956321552L; 
	public GetElecDeclList()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String APL_KIND = getRequest().getParameter("APL_KIND");
		if(SysUtility.isNotEmpty(APL_KIND))
		{
			AddToSearchTable("APL_KIND",APL_KIND);
		}
		this.AddToSearchTable("part_id", SysUtility.getCurrentPartId());
		ReturnWriter(GetReturnDatas("GetElecDeclList").toString());
	}
}
