package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspAttByID")
public class GetInspAttByID extends MainServlet {
	private static final long serialVersionUID = -6042077906444617987L;
	
	public GetInspAttByID(){
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception{
		InitFormData("AttData", SQLMap.getSelect("commongoodsATTsByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString()); 
	}

}
