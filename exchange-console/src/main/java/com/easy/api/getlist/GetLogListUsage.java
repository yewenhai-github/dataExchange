package com.easy.api.getlist;

import com.easy.app.utility.ExpUtility;
import com.easy.session.SessionManager;
import com.easy.web.MainServlet;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.io.PrintWriter;
import java.text.NumberFormat;

@WebServlet("/GetLogListUsage")
public class GetLogListUsage extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		JSONObject getLogListCount = GetReturnDatasAllDB("GetLogListCount");

		AddToSearchTable("MSG_STATUS","1");
		JSONObject getLogListSuccessUsage = GetReturnDatasAllDB("GetLogListUsage");
		AddToSearchTable("MSG_STATUS","0");
		JSONObject getLogListFailUsage = GetReturnDatasAllDB("GetLogListUsage");

		JSONArray getLogListSuccessUsageRows = getLogListSuccessUsage.getJSONArray("rows");
		JSONObject jsonObjectSuccess = getLogListSuccessUsageRows.getJSONObject(0);
		Integer succeeUsage = Integer.valueOf((String)jsonObjectSuccess.get("COUNT"));

		JSONArray getLogListFailUsageRows = getLogListFailUsage.getJSONArray("rows");
		JSONObject jsonObjectFail = getLogListFailUsageRows.getJSONObject(0);
		Integer failUsage = Integer.valueOf((String)jsonObjectFail.get("COUNT"));

		JSONArray getLogListCountRows = getLogListCount.getJSONArray("rows");
		JSONObject jsonObjectList = getLogListCountRows.getJSONObject(0);
		Integer listCount = Integer.valueOf((String)jsonObjectList.get("COUNT"));


		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		String resSucceeUsage = numberFormat.format((float)  succeeUsage/ (float)listCount* 100);
		String resFailUsage = numberFormat.format((float)  failUsage/ (float)listCount* 100);

        JSONObject json = new JSONObject();
		json.put("succeeUsage",resSucceeUsage);
		json.put("failUsage",resFailUsage);
		PrintWriter ResponseWriter = SessionManager.getResponse().getWriter();
		ResponseWriter.print(json);
	}

}