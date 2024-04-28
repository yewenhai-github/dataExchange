package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetInspGoodsPackageByID")
public class GetInspGoodsPackageByID extends MainServlet {
	private static final long serialVersionUID = -4332475593800690377L;
	public GetInspGoodsPackageByID(){
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception{
		InitFormData("PackType", SQLMap.getSelect("GetcommonPackTypeInfoByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString()); 
	}

}
