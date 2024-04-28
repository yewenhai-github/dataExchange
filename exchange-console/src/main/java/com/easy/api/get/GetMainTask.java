package com.easy.api.get;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMainTask")
public class GetMainTask extends MainServlet {
	private static final long serialVersionUID = 7708330752325002855L;

	public GetMainTask() {
		CheckLogin = false;
	}
	
	public void DoCommand() throws Exception{
		JSONObject data = GetReturnDatasAllDB("GetMainTask");
		
		JSONObject rtrow = new JSONObject();
		JSONArray rows = (JSONArray)data.get("rows");
		for (int i = 0; i < rows.length(); i++) {
			JSONObject row = (JSONObject)rows.get(i);
			setRtColumnCount("TOTAL", row, rtrow);
			setRtColumnCount("XMLTODB_1", row, rtrow);
			setRtColumnCount("XMLTODB_2", row, rtrow);
			setRtColumnCount("XMLTODB_3", row, rtrow);
			setRtColumnCount("DBTOXML_1", row, rtrow);
			setRtColumnCount("DBTOXML_2", row, rtrow);
			setRtColumnCount("FAIL_1", row, rtrow);
			setRtColumnCount("FAIL_2", row, rtrow);
			setRtColumnCount("FAIL_3", row, rtrow);
		}
		JSONArray rtrows = new JSONArray();
		rtrows.put(rtrow);
		getFormDatas().put("TaskData", rtrows);
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