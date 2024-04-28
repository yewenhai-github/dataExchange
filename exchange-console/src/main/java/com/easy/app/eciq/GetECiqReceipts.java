package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetECiqReceipts")
public class GetECiqReceipts extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public GetECiqReceipts()
	{
		SetCheckLogin(false);
	}
	public void DoCommand() throws Exception{
		String declNo = getRequest().getParameter("declNo");

		AddToSearchTable("DECL_NO", declNo);
		ReturnWriter(GetReturnDatas("GetECiqReceipts").toString()); 
	}
			
}