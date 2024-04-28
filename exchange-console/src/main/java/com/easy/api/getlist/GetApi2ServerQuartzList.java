package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetApi2ServerQuartzList")
public class GetApi2ServerQuartzList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		JSONObject rows = GetReturnDatasAllDB("GetApi2ServerQuartzList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}