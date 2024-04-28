package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspContainerList")
public class GetInspContainerList extends MainServlet {

	private static final long serialVersionUID = 4168610155645232707L;
	
	public GetInspContainerList()
	{
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception {
		this.AddToSearchTable("DECL_NO", this.GetEnvDatas("DECL_NO"));
		ReturnWriter(GetReturnDatas("GetInspContainerList").toString());
	}


}
