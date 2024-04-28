package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetDeclLimitsList")
public class GetDeclLimitsList extends MainServlet {

	private static final long serialVersionUID = 7476354580758422345L;

	public GetDeclLimitsList(){
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception{
		AddToSearchTable("DECL_NO", this.GetEnvDatas("DECL_NO"));
		ReturnWriter(GetReturnDatas("GetEntLimitsList").toString());
	}
}
