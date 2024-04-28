package com.easy.api.report;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMainCount")
public class GetMainCount extends MainServlet {
	private static final long serialVersionUID = 7708330752325002855L;

	public GetMainCount() {
		CheckLogin = false;
	}
	
	/*******************累加不同数据库下的数据************************/
	public void DoCommand() throws Exception{
		JSONObject data = GetReturnDatasAllDB("GetMainCount");
		
		JSONObject rtrow = new JSONObject();
		if(!data.has("rows")) {
			ReturnWriter(rtrow.toString());
		}
		JSONArray rows = (JSONArray)data.get("rows");
		for (int i = 0; i < rows.length(); i++) {
			JSONObject row = (JSONObject)rows.get(i);
			setRtColumnCount("CONFIG_SUCCESS", row, rtrow);
			setRtColumnCount("CONFIG_FAIL", row, rtrow);
			setRtColumnCount("WARN_SUCCESS", row, rtrow);
			setRtColumnCount("WARN_FAIL", row, rtrow);
			setRtColumnCount("MONITOR_SUCCESS", row, rtrow);
			setRtColumnCount("MONITOR_FAIL", row, rtrow);
		}
		ReturnWriter(rtrow.toString());
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