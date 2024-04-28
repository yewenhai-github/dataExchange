package com.easy.api.getlist;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;

@WebServlet("/GetExchangNodeManageList")
public class GetExchangNodeManageList extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String type= getRequest().getParameter("type");
		if(SysUtility.isNotEmpty(type)){
			AddToSearchTable("EXCEPTIONS", SysUtility.getSysDate().toString());
	    }
		JSONObject rows = GetReturnDatasAllDB("GetExchangNodeManageList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}

}