package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.query.SQLMap;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspGoodsContByID")
public class GetInspGoodsContByID extends MainServlet {
	private static final long serialVersionUID = -2915401212536767368L;
	
	public GetInspGoodsContByID(){
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception{
		InitFormData("goodsContData", SQLMap.getSelect("GetcommonGoodsContInfoByIndx"));
		ReturnMessage(true, "", "", getFormDatas().toString()); 
	}
}
