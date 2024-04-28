package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetInspGoodsLimitsByID")
public class GetInspGoodsLimitsByID extends MainServlet {
	private static final long serialVersionUID = -7933388627177866465L;
	public GetInspGoodsLimitsByID(){
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		InitFormData("LimitData", SQLMap.getSelect("GetGoodsLimitJsonByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString()); 
	}
}
