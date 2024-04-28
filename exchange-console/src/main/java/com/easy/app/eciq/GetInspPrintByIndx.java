package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.exception.LegendException;
import com.easy.query.SQLMap;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetInspPrintByIndx")
public class GetInspPrintByIndx extends MainServlet {

	private static final long serialVersionUID = 7002448616421263447L;

	public GetInspPrintByIndx(){
		this.SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception{
		InitFormData("inspDataPrint",SQLMap.getSelect("GetInspPrintById"));
		InitFormData("InspGoodsPrint",SQLMap.getSelect("GetInspGoodsListPrint"));
		InitFormData("InspContPrint","select CNTNR_MODE_NAME || '/' || CONTAINER_QTY ||'/' || CONT_NO AS CONTS from Itf_Dcl_Io_Decl_Cont A where decl_no= @DECL_NO");
		StringBuilder conts = new StringBuilder();
		JSONArray rows = getFormDatas().getJSONArray("InspContPrint");
		String lk = "";
		for(int i = 0 ; i < rows.length() ; i++){
			conts.append(lk);
			conts.append(rows.getJSONObject(i).getString("CONTS"));
			lk = ";";
		}
		JSONArray rows1 = getFormDatas().getJSONArray("InspGoodsPrint");
		String MNUFCTR_REG_NO ="";
		if(rows1.length()>0){
			MNUFCTR_REG_NO =rows1.getJSONObject(0).getString("MNUFCTR_REG_NO");
		}
		
		 
		SaveToTable("inspDataPrint","MNUFCTR_REG_NO",MNUFCTR_REG_NO);
		SaveToTable("inspDataPrint","CONTS",conts.toString());
		JSONArray goodsRows = getFormDatas().getJSONArray("InspGoodsPrint");
		if(goodsRows.length()>5){
			InitFormData("InspGoodsPrint","SELECT '详见附件' as VIEWNULL FROM dual");
		}
		ReturnMessage(true,"","",getFormDatas().toString());
	}
}
