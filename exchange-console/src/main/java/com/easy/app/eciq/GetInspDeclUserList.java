package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspDeclUserList")
public class GetInspDeclUserList extends MainServlet {

	private static final long serialVersionUID = -7499684143564422110L;
	
	public GetInspDeclUserList(){
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		AddToSearchTable("DECL_NO", this.GetEnvDatas("DECL_NO"));
		ReturnWriter(GetReturnDatas("GetcommonDeclUserListByPIndx").toString());
	}

}
