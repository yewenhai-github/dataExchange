package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.exception.LegendException;
import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspLimitsByID")
public class GetInspLimitsByID extends MainServlet {

	private static final long serialVersionUID = -6042077906444617987L;
	
	public GetInspLimitsByID(){
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception{
		InitFormData("DeclLimitData", SQLMap.getSelect("GetcommonEntLimitInfoByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString()); 
	}

}
