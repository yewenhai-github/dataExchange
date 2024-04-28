package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetGoodsByEditContain")
public class GetGoodsByEditContain  extends MainServlet{  
	private static final long serialVersionUID = 3594076222956321552L; 
	public GetGoodsByEditContain()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception{
		String DECL_NO = getRequest().getParameter("DECL_NO");
		String hscode=GetDataValue("SearchGoods", "PROD_HS_CODE");
		String gcname=GetDataValue("SearchGoods", "DECL_GOODS_CNAME");
		String ciqcode=GetDataValue("SearchGoods", "CIQ_CODE");
		 	
		if(SysUtility.isEmpty(DECL_NO))
		{
			ReturnMessage(false, "参数错误");
		}
		this.AddToSearchTable("DECL_NO", DECL_NO);
		this.AddToSearchTable("PROD_HS_CODE", hscode);
		this.AddToSearchTable("DECL_GOODS_CNAME", gcname);
		this.AddToSearchTable("CIQ_CODE", ciqcode);
		ReturnWriter(GetReturnDatas("GetGoodsByEditContain").toString());
	}
}
