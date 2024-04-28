package com.easy.app.eciq;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.exception.LegendException;
import com.easy.query.SQLMap;
import com.easy.web.MainServlet;
@WebServlet("/forms/GetCheckPrint")
public class GetCheckPrint extends MainServlet {

	private static final long serialVersionUID = 7002448616421263447L;

	public GetCheckPrint(){
		this.SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception{
		InitFormData("inspDataPrint",SQLMap.getSelect("GetCheckPrint"));
		ReturnMessage(true,"","",getFormDatas().toString());
	}
}
