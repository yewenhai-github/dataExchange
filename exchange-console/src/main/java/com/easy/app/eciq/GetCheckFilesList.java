package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetCheckFilesList")
public class GetCheckFilesList extends MainServlet{
	private static final long serialVersionUID = 1564067536519132801L;
	public void DoCommand() throws Exception{
		String type = getRequest().getParameter("type");
		String P_INDX = getRequest().getParameter("PIndx");

		AddToSearchTable("P_INDX", P_INDX);
		AddToSearchTable("BIZ_TYPE", type);
		ReturnWriter(GetReturnDatas("GetCheckFilesList").toString());
	}
}
