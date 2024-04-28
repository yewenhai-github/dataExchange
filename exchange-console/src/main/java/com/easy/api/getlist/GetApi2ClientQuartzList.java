package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetApi2ClientQuartzList")
public class GetApi2ClientQuartzList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		JSONObject rows = GetReturnDatasAllDB("GetApi2ClientQuartzList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}