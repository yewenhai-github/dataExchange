package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetAttachedListByDeclGetNo")
public class GetAttachedListByDeclGetNo  extends MainServlet{  
	private static final long serialVersionUID = 3594076222956321552L; 
	public GetAttachedListByDeclGetNo()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String DECL_GET_NO=(String) this.GetEnvDatas("DECL_GET_NO");
		AddToSearchTable("DECL_GET_NO",DECL_GET_NO);
		ReturnWriter(GetReturnDatas("GetAttachedListByDeclGetNo").toString());
	}
}
