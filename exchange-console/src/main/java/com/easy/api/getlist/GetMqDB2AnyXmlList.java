package com.easy.api.getlist;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.easy.app.utility.ExpUtility;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

@WebServlet("/GetMqDB2AnyXmlList")
public class GetMqDB2AnyXmlList  extends MainServlet {
	private static final long serialVersionUID = 1564067536519132801L;

	public void DoCommand() throws Exception{
		String type= getRequest().getParameter("type");
		if(SysUtility.isNotEmpty(type)){
			AddToSearchTable("EXCEPTIONS", SysUtility.getSysDate().toString());
	    }
		AddToSearchTable("PRODUCER_URL", "0");//MQ生产者地址不为空
		AddToSearchTable("DATA_TYPE", new String[] {"AnyJson","AnyXml","EdiFact"});
		JSONObject rows = GetReturnDatasAllDB("GetDB2AnyXmlList");
		ExpUtility.setRowsDefault(rows);
		ReturnWriter(rows.toString());
	}
}