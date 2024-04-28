package com.easy.app.eciq;
import javax.servlet.annotation.WebServlet;

import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspGoodsLimitsList")
public class GetInspGoodsLimitsList  extends MainServlet{  
	
	private static final long serialVersionUID = 8524100130045022405L;
	
	public GetInspGoodsLimitsList()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		AddToSearchTable("DECL_NO", this.GetEnvDatas("DECL_NO"));
		AddToSearchTable("GOODS_ID", this.GetEnvDatas("Goods_Id"));
		ReturnWriter(GetReturnDatas("commongoodsLimitsList").toString());	
	}
}