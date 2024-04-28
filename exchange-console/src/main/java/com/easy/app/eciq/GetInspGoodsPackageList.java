package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspGoodsPackageList")

public class GetInspGoodsPackageList extends MainServlet {
	
	private static final long serialVersionUID = 7862084007305954221L;
	
	public GetInspGoodsPackageList(){
		this.SetCheckLogin(true);
	}

	public void DoCommand() throws Exception {
		AddToSearchTable("GOODS_ID", this.GetEnvDatas("GoodsId"));
		AddToSearchTable("DECL_NO", this.GetEnvDatas("DECL_NO"));
		ReturnWriter(GetReturnDatas("GetcommonPackTypeListByPIndx").toString());
	}
}
