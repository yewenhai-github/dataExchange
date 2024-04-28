package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetXml2MqList")
public class GetXml2MqList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String type= getRequest().getParameter("type");
		if(SysUtility.isNotEmpty(type)){
			AddToSearchTable("EXCEPTIONS", SysUtility.getSysDate().toString());
	    }
		JSONObject rows = GetReturnDatasAllDB("GetXml2MqList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}