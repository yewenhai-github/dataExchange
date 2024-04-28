package com.easy.api.report;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetPortalConfigApi")
public class GetPortalConfigApi extends MainServlet {
	private static final long serialVersionUID = 7708330752325002855L;

	public GetPortalConfigApi() {
		CheckLogin = false;
	}
	
	public void DoCommand() throws Exception{
		JSONObject data = GetReturnDatasAllDB("GetPortalConfigApi");
		
		JSONObject rtrow = new JSONObject();
		JSONArray rows = (JSONArray)data.get("rows");
		for (int i = 0; i < rows.length(); i++) {
			JSONObject row = (JSONObject)rows.get(i);
			setRtColumnCount("API_PULL_SUCCESS", row, rtrow);
			setRtColumnCount("API_PUSH_SUCCESS", row, rtrow);
			setRtColumnCount("API_QUARTZ_PUSH_SUCCESS", row, rtrow);
			setRtColumnCount("API_QUARTZ_PULL_SUCCESS", row, rtrow);
			setRtColumnCount("API_PUSH_FAIL", row, rtrow);
			setRtColumnCount("API_PULL_FAIL", row, rtrow);
			setRtColumnCount("API_QUARTZ_PUSH_FAIL", row, rtrow);
			setRtColumnCount("API_QUARTZ_PULL_FAIL", row, rtrow);
		}
		JSONArray rtrows = new JSONArray();
		rtrows.put(rtrow);
		getFormDatas().put("PortalConfigApi", rtrows);
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