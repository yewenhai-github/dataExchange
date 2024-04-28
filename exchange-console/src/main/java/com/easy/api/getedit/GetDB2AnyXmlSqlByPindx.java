package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetDB2AnyXmlSqlByPindx")
public class GetDB2AnyXmlSqlByPindx extends MainServlet {
	private static final long serialVersionUID = 9134450017008255141L;

	public void DoCommand() throws Exception{
		String INDX= getRequest().getParameter("INDX");
		AddToSearchTable("P_INDX", INDX);
		Datas data = (Datas) GetReturnDatas("GetDB2AnyXmlSqlByPindx");
		if(SysUtility.IsMySqlDB() && SysUtility.isNotEmpty(data) && data.has("rows")) {
    		data.put("rows", SysUtility.JSONArrayToUpperCase((JSONArray)data.get("rows")));
    	}
		ReturnWriter(data.toString());
	}

}
