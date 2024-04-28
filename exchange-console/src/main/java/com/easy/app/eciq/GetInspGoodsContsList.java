package com.easy.app.eciq;


import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspGoodsContsList")
public class GetInspGoodsContsList extends MainServlet {
	private static final long serialVersionUID = -2663720501470484126L;

	public GetInspGoodsContsList(){
		this.SetCheckLogin(true);
	}
	
	public void DoCommand() throws Exception {
		AddToSearchTable("GOODS_ID", this.GetEnvDatas("GoodsId"));
		AddToSearchTable("DECL_NO", this.GetEnvDatas("DECL_NO"));
		ReturnWriter(GetReturnDatas("GetcommonGoodsContListByPIndx").toString());
	}
}
