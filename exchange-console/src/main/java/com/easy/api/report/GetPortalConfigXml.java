package com.easy.api.report;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetPortalConfigXml")
public class GetPortalConfigXml extends MainServlet {
	private static final long serialVersionUID = 7708330752325002855L;

	public GetPortalConfigXml() {
		CheckLogin = false;
	}
	
	public void DoCommand() throws Exception{
		JSONObject data = GetReturnDatasAllDB("GetPortalConfigXml");
		
		JSONObject rtrow = new JSONObject();
		JSONArray rows = (JSONArray)data.get("rows");
		for (int i = 0; i < rows.length(); i++) {
			JSONObject row = (JSONObject)rows.get(i);
			setRtColumnCount("DBTOXML_SUCCESS", row, rtrow);
			setRtColumnCount("XMLTOMQ_SUCCESS", row, rtrow);
			setRtColumnCount("MQTOXML_SUCCESS", row, rtrow);
			setRtColumnCount("XMLTODB_SUCCESS", row, rtrow);
			setRtColumnCount("DBTOXML_FAIL", row, rtrow);
			setRtColumnCount("XMLTOMQ_FAIL", row, rtrow);
			setRtColumnCount("MQTOXML_FAIL", row, rtrow);
			setRtColumnCount("XMLTODB_FAIL", row, rtrow);
		}
		JSONArray rtrows = new JSONArray();
		rtrows.put(rtrow);
		getFormDatas().put("PortalConfigXml", rtrows);
		ExpUtility.setRowsDefault(getFormDatas());
		ReturnMessage(true, "", "", getFormDatas().toString());
	}

	private void setRtColumnCount(String columnName, JSONObject row, JSONObject rtrow) throws JSONException {
		if(!rtrow.has(columnName)) {
			rtrow.put(columnName, SysUtility.getJsonField(row, columnName));
		}else {
			int ivalue = Integer.parseInt(SysUtility.getJsonField(row, columnName));
			int rvalue = Integer.parseInt(SysUtility.getJsonField(rtrow, columnName));
			rtrow.put(columnName, ivalue+rvalue);
		}
	}

}