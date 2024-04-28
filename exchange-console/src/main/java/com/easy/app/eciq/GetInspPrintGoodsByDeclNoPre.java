package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.exception.LegendException;
import com.easy.query.SQLMap;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetInspPrintGoodsByDeclNoPre")
public class GetInspPrintGoodsByDeclNoPre extends MainServlet {

	private static final long serialVersionUID = 7002448616421263447L;

	public GetInspPrintGoodsByDeclNoPre(){
		this.SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception{
		InitFormData("inspDataPrint",SQLMap.getSelect("GetInspPrintById"));
		InitFormData("InspGoodsPrintSize",SQLMap.getSelect("GetInspGoodsListPrintAll"));
		InitFormData("InspGoodsPrint",SQLMap.getSelect("GetInspGoodsListPrintPre"));
		//货物总值
		InitFormData("InspGoodsPrintTotalValSum","SELECT SUM(GOODS_TOTAL_VAL) AS TOTALSUM,CURRENCN FROM ITF_DCL_IO_DECL_GOODS WHERE DECL_NO=@DECL_NO GROUP BY CURRENCN");
		StringBuilder TotalValSum = new StringBuilder();
		JSONArray rows = getFormDatas().getJSONArray("InspGoodsPrintTotalValSum");
		String lk = "";
		for(int i = 0 ; i < rows.length() ; i++){
			TotalValSum.append(lk);
			TotalValSum.append(rows.getJSONObject(i).getString("TOTALSUM")+rows.getJSONObject(i).getString("CURRENCN"));
			lk = ";";
		}
		SaveToTable("InspGoodsPrintTotalValSum","TotalValSum",TotalValSum.toString());
		//货物包装合计
		InitFormData("InspGoodsPrintPackSum","select SUM(P.PACK_QTY) AS PACKQTY, P.PACK_CATG_NAME AS PACK_CATG from  ITF_DCL_IO_DECL_GOODS G LEFT JOIN ITF_DCL_IO_DECL_GOODS_PACK P ON G.GOODS_ID = P.GOODS_ID AND P.IS_MAIN_PACK = 1 WHERE G.DECL_NO=@DECL_NO GROUP BY PACK_CATG_NAME");
		StringBuilder PackSum = new StringBuilder();
		JSONArray rows1 = getFormDatas().getJSONArray("InspGoodsPrintPackSum");
		String lk1 = "";
		for(int i = 0 ; i < rows1.length() ; i++){
			PackSum.append(lk1);
			PackSum.append(rows1.getJSONObject(i).getString("PACKQTY")+rows1.getJSONObject(i).getString("PACK_CATG"));
			lk1 = ";";
		}
		SaveToTable("InspGoodsPrintPackSum","PackSum",PackSum.toString());
		
		JSONArray goodsRows = getFormDatas().getJSONArray("InspGoodsPrintSize");
		SaveToTable("InspGoodsPrint","goodsSize", String.valueOf(goodsRows.length()));
		ReturnMessage(true,"","",getFormDatas().toString());
	}
}
