package com.easy.api.getedit;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;

import com.easy.access.Datas;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;
              
@WebServlet("/GetAnyXml2DBSqlByPindx")
public class GetAnyXml2DBSqlByPindx extends MainServlet {
	private static final long serialVersionUID = 380114553299209159L;

	public void DoCommand() throws Exception{
		String INDX= getRequest().getParameter("INDX");
		AddToSearchTable("P_INDX", INDX);
		Datas data = (Datas) GetReturnDatas("GetAnyXml2DBSqlByPindx");
		if(SysUtility.IsMySqlDB() && SysUtility.isNotEmpty(data) && data.has("rows")) {
    		data.put("rows", SysUtility.JSONArrayToUpperCase((JSONArray)data.get("rows")));
    	}
		ReturnWriter(data.toString());
	}

}
