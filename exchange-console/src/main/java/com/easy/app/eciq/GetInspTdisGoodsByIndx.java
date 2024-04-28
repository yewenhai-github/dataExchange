package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.query.SQLMap;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/forms/GetInspTdisGoodsByIndx")
public class GetInspTdisGoodsByIndx extends MainServlet {

	private static final long serialVersionUID = -5351665045925757934L;
	public GetInspTdisGoodsByIndx()
	{
		this.SetCheckLogin(true);
	}
	public void DoCommand() throws Exception {
		String allId=getRequest().getParameter("allId");
		String GOODS_ID=getRequest().getParameter("GOODS_ID");
		
		if(SysUtility.isNotEmpty(allId)){
			AddToSearchTable("PINDX", allId.split(","));
		}
		if(SysUtility.isNotEmpty(GOODS_ID)){
			AddToSearchTable("GOODS_ID", GOODS_ID);
		}
		
		JSONObject Data=GetReturnDatas("GetInspTdisGoodsByIndx","GoodsData");
		getFormDatas().put("GoodsData", Data.get("GoodsData"));
		ReturnMessage(true, "", "", getFormDatas().toString());
	}
}
