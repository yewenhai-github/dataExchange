package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetInspUsersByID")
public class GetInspUsersByID extends MainServlet {
	private static final long serialVersionUID = -6042077906444617987L;
	
	public GetInspUsersByID(){
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception{
		InitFormData("DeclUserData", SQLMap.getSelect("GetcommonDeclUserInfoByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString()); 
	}

}
